package be.Lombardi.dao;

import java.sql.*;

import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.*;

public class PersonDAO extends DAO<Person> {

    public PersonDAO(Connection conn) {
        super(conn);
    }

    
    // Cette méthode est juste là au cas où on voudrait créer une Personne qui n'a pas de rôle
    @Override
    public boolean create(Person person) throws DAOException {
        final String SQL = "INSERT INTO Person (name, firstname, tel, username, password, role) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setString(1, person.getName());
            ps.setString(2, person.getFirstname());
            ps.setString(3, person.getTel());
            ps.setString(4, person.getUsername());
            ps.setString(5, person.getPassword());
            ps.setString(6, "PERSON");
            
            return ps.executeUpdate() > 0;
            }
        catch (SQLException e) {
            throw new DAOException("Erreur lors de la création de la personne", e);
        }
    } 

    @Override
    public boolean delete(Person person) throws DAOException {
        final String SQL = "DELETE FROM Person WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, person.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de la personne", e);
        }
    }

    @Override
    public boolean update(Person person) throws DAOException {
        final String SQL = """
            UPDATE Person
            SET name = ?, firstname = ?, tel = ?, username = ?
            WHERE person_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setString(1, person.getName());
            ps.setString(2, person.getFirstname());
            ps.setString(3, person.getTel());
            ps.setString(4, person.getUsername());
            ps.setInt(5, person.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de la personne", e);
        }
    }

    @Override
    public Person find(int id) throws DAOException {
        final String SQL = "SELECT person_id, name, firstname, tel, username FROM Person WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Person(
                        rs.getInt("person_id"),
                        getSafe(rs, "name"),
                        getSafe(rs, "firstname"),
                        getSafe(rs, "tel"),
                        getSafe(rs, "username")
                    ) {
                        // Classe anonyme car Person est abstraite
                    };
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de la personne", e);
        }
        
        return null;
    }

    public Person login(String username, String password) throws DAOException {
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
                    String role = getSafe(rs, "role");

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

    private String getSafe(ResultSet rs, String col) {
        try {
            String v = rs.getString(col);
            return v != null ? v : "";
        } catch (SQLException e) {
            return "";
        }
    }
}