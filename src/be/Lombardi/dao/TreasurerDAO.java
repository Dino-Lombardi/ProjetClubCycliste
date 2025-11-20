package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.Treasurer;

public class TreasurerDAO extends DAO<Treasurer> {

    public TreasurerDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Treasurer treasurer) {
        AbstractDAOFactory factory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        MemberDAO memberDAO = (MemberDAO) factory.getMemberDAO();
        ManagerDAO managerDAO = (ManagerDAO) factory.getManagerDAO();
        
        if (memberDAO.find(treasurer.getId()) != null) {
            throw new IllegalStateException("Cette personne est déjà Member. Un Member ne peut pas être Treasurer.");
        }
        
        if (managerDAO.find(treasurer.getId()) != null) {
            throw new IllegalStateException("Cette personne est déjà Manager.");
        }
        
        final String SQL = "INSERT INTO Treasurer (person_id) VALUES (?)";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, treasurer.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du trésorier", e);
        }
    }

    @Override
    public boolean delete(Treasurer treasurer) {
        final String SQL = "DELETE FROM Treasurer WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, treasurer.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du trésorier", e);
        }
    }

    @Override
    public boolean update(Treasurer treasurer) {
        return true;
    }

    @Override
    public Treasurer find(int id) {
        final String SQL = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username
            FROM Person p
            JOIN Treasurer t ON t.person_id = p.person_id
            WHERE p.person_id = ?
            """;

        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Treasurer(
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getString("firstname"),
                        rs.getString("tel"),
                        rs.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération du trésorier", e);
        }
        
        return null;
    }
}