package be.Lombardi.dao;

import java.sql.*;

import be.Lombardi.pojo.*;

public class TreasurerDAO extends DAO<Treasurer> {

    public TreasurerDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Treasurer treasurer) throws DAOException {
        final String SQL_PERSON = "INSERT INTO Person (name, firstname, tel, username) VALUES (?, ?, ?, ?)";
        final String SQL_TREASURER = "INSERT INTO Treasurer (person_id) VALUES (?)";
        
        try (PreparedStatement psPerson = connect.prepareStatement(SQL_PERSON, Statement.RETURN_GENERATED_KEYS)) {
            psPerson.setString(1, treasurer.getName());
            psPerson.setString(2, treasurer.getFirstname());
            psPerson.setString(3, treasurer.getTel());
            psPerson.setString(4, treasurer.getUsername());
            
            int rowsAffected = psPerson.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = psPerson.getGeneratedKeys()) {
                    if (rs.next()) {
                        int personId = rs.getInt(1);
                        treasurer.setId(personId);
                        
                        try (PreparedStatement psTreasurer = connect.prepareStatement(SQL_TREASURER)) {
                            psTreasurer.setInt(1, personId);
                            return psTreasurer.executeUpdate() > 0;
                        }
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du trésorier", e);
        }
    }

    @Override
    public boolean delete(Treasurer treasurer) throws DAOException {
        final String SQL = "DELETE FROM Person WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, treasurer.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du trésorier", e);
        }
    }

    @Override
    public boolean update(Treasurer treasurer) throws DAOException {
        final String SQL = """
            UPDATE Person
            SET name = ?, firstname = ?, tel = ?, username = ?
            WHERE person_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setString(1, treasurer.getName());
            ps.setString(2, treasurer.getFirstname());
            ps.setString(3, treasurer.getTel());
            ps.setString(4, treasurer.getUsername());
            ps.setInt(5, treasurer.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du trésorier", e);
        }
    }

    @Override
    public Treasurer find(int id) throws DAOException {
        final String SQL = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username
            FROM Treasurer t
            JOIN Person p ON t.person_id = p.person_id
            WHERE t.person_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Treasurer(
                        rs.getInt("person_id"),
                        getSafe(rs, "name"),
                        getSafe(rs, "firstname"),
                        getSafe(rs, "tel"),
                        getSafe(rs, "username")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du trésorier", e);
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