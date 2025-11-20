package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.Person;

public class PersonDAO extends DAO<Person> {

    public PersonDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Person obj) {
        final String SQL = """
            INSERT INTO Person (name, firstname, tel, username, password, role)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setString(1, obj.getName());
            ps.setString(2, obj.getFirstname());
            ps.setString(3, obj.getTel());
            ps.setString(4, obj.getUsername());
            ps.setString(5, "defaultPassword");
            ps.setString(6, "MEMBER");
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création de la personne", e);
        }
    }

    @Override
    public boolean delete(Person obj) {
        final String SQL = "DELETE FROM Person WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de la personne", e);
        }
    }

    @Override
    public boolean update(Person obj) {
        final String SQL = """
            UPDATE Person
            SET name = ?, firstname = ?, tel = ?, username = ?
            WHERE person_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setString(1, obj.getName());
            ps.setString(2, obj.getFirstname());
            ps.setString(3, obj.getTel());
            ps.setString(4, obj.getUsername());
            ps.setInt(5, obj.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de la personne", e);
        }
    }

    @Override
    public Person find(int id) {
        return null;
    }

    public Person login(String username, String password) {
        final String SQL = """
            SELECT person_id, role
            FROM Person
            WHERE username = ? AND password = ?
            """;

        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("person_id");
                    String role = rs.getString("role");

                    AbstractDAOFactory factory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);

                    switch (role.toUpperCase()) {
                        case "MEMBER":
                            return ((MemberDAO) factory.getMemberDAO()).find(id);

                        case "MANAGER":
                            return ((ManagerDAO) factory.getManagerDAO()).find(id);

                        case "TREASURER":
                            return ((TreasurerDAO) factory.getTreasurerDAO()).find(id);

                        default:
                            return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la connexion", e);
        }

        return null;
    }
}