package be.Lombardi.dao;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import be.Lombardi.pojo.*;

public class MemberDAO extends DAO<Member> {

    public MemberDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Member member) throws DAOException {
        final String SQL_PERSON = "INSERT INTO Person (name, firstname, tel, username) VALUES (?, ?, ?, ?)";
        final String SQL_MEMBER = "INSERT INTO Member (person_id, balance) VALUES (?, ?)";
        
        try (PreparedStatement psPerson = connect.prepareStatement(SQL_PERSON, Statement.RETURN_GENERATED_KEYS)) {
            psPerson.setString(1, member.getName());
            psPerson.setString(2, member.getFirstname());
            psPerson.setString(3, member.getTel());
            psPerson.setString(4, member.getUsername());
            
            int rowsAffected = psPerson.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = psPerson.getGeneratedKeys()) {
                    if (rs.next()) {
                        int personId = rs.getInt(1);
                        member.setId(personId);
                        
                        try (PreparedStatement psMember = connect.prepareStatement(SQL_MEMBER)) {
                            psMember.setInt(1, personId);
                            psMember.setDouble(2, member.getBalance());
                            return psMember.executeUpdate() > 0;
                        }
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du membre", e);
        }
    }

    @Override
    public boolean delete(Member member) throws DAOException {
        final String SQL = "DELETE FROM Person WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, member.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du membre", e);
        }
    }

    @Override
    public boolean update(Member member) throws DAOException {
        final String SQL_PERSON = """
            UPDATE Person
            SET name = ?, firstname = ?, tel = ?, username = ?
            WHERE person_id = ?
            """;
        final String SQL_MEMBER = "UPDATE Member SET balance = ? WHERE person_id = ?";
        
        try (PreparedStatement psPerson = connect.prepareStatement(SQL_PERSON)) {
            psPerson.setString(1, member.getName());
            psPerson.setString(2, member.getFirstname());
            psPerson.setString(3, member.getTel());
            psPerson.setString(4, member.getUsername());
            psPerson.setInt(5, member.getId());
            
            int rowsAffected = psPerson.executeUpdate();
            
            if (rowsAffected > 0) {
                try (PreparedStatement psMember = connect.prepareStatement(SQL_MEMBER)) {
                    psMember.setDouble(1, member.getBalance());
                    psMember.setInt(2, member.getId());
                    return psMember.executeUpdate() > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du membre", e);
        }
    }

    @Override
    public Member find(int id) throws DAOException {
        final String SQL = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username,
                   m.balance
            FROM Member m
            JOIN Person p ON m.person_id = p.person_id
            WHERE m.person_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member(
                        rs.getInt("person_id"),
                        getSafe(rs, "name"),
                        getSafe(rs, "firstname"),
                        getSafe(rs, "tel"),
                        getSafe(rs, "username"),
                        rs.getDouble("balance")
                    );
                    
                    loadCategoriesForMember(member);
                    
                    return member;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du membre", e);
        }
        
        return null;
    }

    private void loadCategoriesForMember(Member member) throws DAOException {
        final String SQL = "SELECT category FROM MemberCategory WHERE person_id = ?";
        
        Set<CategoryType> categories = new HashSet<>();
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, member.getId());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String categoryStr = getSafe(rs, "category");
                    if (!categoryStr.isEmpty()) {
                        CategoryType category = CategoryType.valueOf(categoryStr.toUpperCase());
                        categories.add(category);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors du chargement des catégories du membre", e);
        }
        
        member.setCategories(categories);
    }

    private String getSafe(ResultSet rs, String col) {
        try {
            String v = rs.getString(col);
            return v != null ? v : "";
        } catch (SQLException e) {
            return "";
        }
    }
}