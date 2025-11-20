package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.CategoryType;
import be.Lombardi.pojo.Member;

public class MemberDAO extends DAO<Member> {

    public MemberDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Member member) {
        AbstractDAOFactory factory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        ManagerDAO managerDAO = (ManagerDAO) factory.getManagerDAO();
        TreasurerDAO treasurerDAO = (TreasurerDAO) factory.getTreasurerDAO();
        
        if (managerDAO.find(member.getId()) != null) {
            throw new IllegalStateException("Cette personne est déjà Manager. Un Manager ne peut pas être Member.");
        }
        
        if (treasurerDAO.find(member.getId()) != null) {
            throw new IllegalStateException("Cette personne est déjà Treasurer. Un Treasurer ne peut pas être Member.");
        }
        
        final String SQL = "INSERT INTO Member (person_id, balance) VALUES (?, ?)";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, member.getId());
            ps.setDouble(2, member.getBalance());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du membre", e);
        }
    }

    @Override
    public boolean delete(Member member) {
        final String SQL = "DELETE FROM Member WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, member.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du membre", e);
        }
    }

    @Override
    public boolean update(Member member) {
        final String SQL = "UPDATE Member SET balance = ? WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setDouble(1, member.getBalance());
            ps.setInt(2, member.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du membre", e);
        }
    }

    @Override
    public Member find(int id) {
        final String SQL_MEMBER = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username,
                   m.balance
            FROM Person p
            JOIN Member m ON m.person_id = p.person_id
            WHERE p.person_id = ?
            """;

        final String SQL_CATEGORIES = """
            SELECT category
            FROM MemberCategory
            WHERE person_id = ?
            """;

        try {
            Member member = null;

            try (PreparedStatement ps = connect.prepareStatement(SQL_MEMBER)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        member = new Member(
                            rs.getInt("person_id"),
                            rs.getString("name"),
                            rs.getString("firstname"),
                            rs.getString("tel"),
                            rs.getString("username"),
                            rs.getDouble("balance")
                        );
                    }
                }
            }

            if (member != null) {
                try (PreparedStatement ps = connect.prepareStatement(SQL_CATEGORIES)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String cat = rs.getString("category");
                            member.addCategory(CategoryType.valueOf(cat.toUpperCase()));
                        }
                    }
                }
            }

            return member;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération du membre", e);
        }
    }

    public boolean syncCategories(Member member) {
        final String SQL_SELECT_CATEGORIES = "SELECT category FROM MemberCategory WHERE person_id = ?";
        final String SQL_INSERT_CATEGORY = "INSERT INTO MemberCategory (person_id, category) VALUES (?, ?)";
        final String SQL_DELETE_CATEGORY = "DELETE FROM MemberCategory WHERE person_id = ? AND category = ?";
        
        try {
            connect.setAutoCommit(false);
            
            Set<CategoryType> dbCategories = new HashSet<>();
            try (PreparedStatement ps = connect.prepareStatement(SQL_SELECT_CATEGORIES)) {
                ps.setInt(1, member.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dbCategories.add(CategoryType.valueOf(rs.getString("category").toUpperCase()));
                    }
                }
            }
            
            Set<CategoryType> pojoCategories = member.getCategories();
            
            Set<CategoryType> toAdd = new HashSet<>(pojoCategories);
            toAdd.removeAll(dbCategories);
            
            Set<CategoryType> toRemove = new HashSet<>(dbCategories);
            toRemove.removeAll(pojoCategories);
            
            if (!toAdd.isEmpty()) {
                try (PreparedStatement ps = connect.prepareStatement(SQL_INSERT_CATEGORY)) {
                    for (CategoryType category : toAdd) {
                        ps.setInt(1, member.getId());
                        ps.setString(2, category.toString());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            if (!toRemove.isEmpty()) {
                try (PreparedStatement ps = connect.prepareStatement(SQL_DELETE_CATEGORY)) {
                    for (CategoryType category : toRemove) {
                        ps.setInt(1, member.getId());
                        ps.setString(2, category.toString());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            connect.commit();
            connect.setAutoCommit(true);
            return true;
            
        } catch (SQLException e) {
            try {
                connect.rollback();
                connect.setAutoCommit(true);
            } catch (SQLException ex) {
                throw new DAOException("Erreur lors du rollback", ex);
            }
            throw new DAOException("Erreur lors de la synchronisation des catégories", e);
        }
    }
}