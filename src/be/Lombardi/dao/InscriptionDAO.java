package be.Lombardi.dao;

import java.sql.*;

import be.Lombardi.pojo.*;

public class InscriptionDAO extends DAO<Inscription> {

    public InscriptionDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Inscription inscription) throws DAOException {
        final String SQL = """
            INSERT INTO Inscription (member_id, ride_id, vehicle_id, bike_id, is_passenger, has_bike)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, inscription.getMember().getId());
            ps.setInt(2, inscription.getRide().getId());
            
            if (inscription.getVehicle() != null) {
                ps.setInt(3, inscription.getVehicle().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            if (inscription.getBike() != null) {
                ps.setInt(4, inscription.getBike().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            
            ps.setBoolean(5, inscription.isPassenger());
            ps.setBoolean(6, inscription.hasBike());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création de l'inscription", e);
        }
    }

    @Override
    public boolean delete(Inscription inscription) throws DAOException {
        final String SQL = "DELETE FROM Inscription WHERE inscription_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, inscription.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de l'inscription", e);
        }
    }

    @Override
    public boolean update(Inscription inscription) throws DAOException {
        final String SQL = """
            UPDATE Inscription
            SET member_id = ?, ride_id = ?, vehicle_id = ?, bike_id = ?, is_passenger = ?, has_bike = ?
            WHERE inscription_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, inscription.getMember().getId());
            ps.setInt(2, inscription.getRide().getId());
            
            if (inscription.getVehicle() != null) {
                ps.setInt(3, inscription.getVehicle().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            if (inscription.getBike() != null) {
                ps.setInt(4, inscription.getBike().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            
            ps.setBoolean(5, inscription.isPassenger());
            ps.setBoolean(6, inscription.hasBike());
            ps.setInt(7, inscription.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de l'inscription", e);
        }
    }

    @Override
    public Inscription find(int id) throws DAOException {
        final String SQL = """
            SELECT i.inscription_id, i.is_passenger, i.has_bike, i.member_id, i.ride_id, i.vehicle_id, i.bike_id,
                   p.name, p.firstname, p.tel, p.username
            FROM Inscription i
            JOIN Person p ON i.member_id = p.person_id
            WHERE i.inscription_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member(
                        rs.getInt("member_id"),
                        getSafe(rs, "name"),
                        getSafe(rs, "firstname"),
                        getSafe(rs, "tel"),
                        getSafe(rs, "username"),
                        0.0
                    );
                    
                    return new Inscription(
                        rs.getInt("inscription_id"),
                        rs.getBoolean("is_passenger"),
                        rs.getBoolean("has_bike"),
                        member,
                        null
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de l'inscription", e);
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