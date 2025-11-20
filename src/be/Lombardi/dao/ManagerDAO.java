package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.CategoryType;
import be.Lombardi.pojo.Manager;

public class ManagerDAO extends DAO<Manager> {

    public ManagerDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Manager manager) {
        AbstractDAOFactory factory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        MemberDAO memberDAO = (MemberDAO) factory.getMemberDAO();
        TreasurerDAO treasurerDAO = (TreasurerDAO) factory.getTreasurerDAO();
        
        if (memberDAO.find(manager.getId()) != null) {
            throw new IllegalStateException("Cette personne est déjà Member. Un Member ne peut pas être Manager.");
        }
        
        if (treasurerDAO.find(manager.getId()) != null) {
            throw new IllegalStateException("Cette personne est déjà Treasurer.");
        }
        
        final String SQL = "INSERT INTO Manager (person_id, category) VALUES (?, ?)";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, manager.getId());
            ps.setString(2, manager.getCategory().toString());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du manager", e);
        }
    }

    @Override
    public boolean delete(Manager manager) {
        final String SQL = "DELETE FROM Manager WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, manager.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du manager", e);
        }
    }

    @Override
    public boolean update(Manager manager) {
        final String SQL = "UPDATE Manager SET category = ? WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setString(1, manager.getCategory().toString());
            ps.setInt(2, manager.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du manager", e);
        }
    }

    @Override
    public Manager find(int id) {
        final String SQL = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username,
                   m.category
            FROM Person p
            JOIN Manager m ON m.person_id = p.person_id
            WHERE p.person_id = ?
            """;

        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Manager(
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getString("firstname"),
                        rs.getString("tel"),
                        rs.getString("username"),
                        CategoryType.valueOf(rs.getString("category").toUpperCase())
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération du manager", e);
        }
        
        return null;
    }
}