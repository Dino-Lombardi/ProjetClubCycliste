package be.Lombardi.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.Lombardi.pojo.*;

public class VehicleDAO extends DAO<Vehicle> {

    public VehicleDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Vehicle vehicle) throws DAOException {
        final String SQL = "INSERT INTO Vehicle (seat_number, bike_spot_number, owner_id) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, vehicle.getSeatNumber());
            ps.setInt(2, vehicle.getBikeSpotNumber());
            ps.setInt(3, vehicle.getOwner().getId());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        vehicle.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du véhicule", e);
        }
    }

    @Override
    public boolean delete(Vehicle vehicle) throws DAOException {
        final String SQL = "DELETE FROM Vehicle WHERE vehicle_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, vehicle.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du véhicule", e);
        }
    }

    @Override
    public boolean update(Vehicle vehicle) throws DAOException {
        final String SQL = """
            UPDATE Vehicle
            SET seat_number = ?, bike_spot_number = ?, owner_id = ?
            WHERE vehicle_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, vehicle.getSeatNumber());
            ps.setInt(2, vehicle.getBikeSpotNumber());
            ps.setInt(3, vehicle.getOwner().getId());
            ps.setInt(4, vehicle.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du véhicule", e);
        }
    }

    @Override
    public Vehicle find(int id) throws DAOException {
        final String SQL = """
            SELECT v.vehicle_id, v.seat_number, v.bike_spot_number, v.owner_id,
                   p.name, p.firstname, p.tel, p.username,
                   m.balance
            FROM Vehicle v
            JOIN Member m ON v.owner_id = m.person_id
            JOIN Person p ON m.person_id = p.person_id
            WHERE v.vehicle_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member owner = new Member(
                        rs.getInt("owner_id"),
                        getSafe(rs, "name"),
                        getSafe(rs, "firstname"),
                        getSafe(rs, "tel"),
                        getSafe(rs, "username"),
                        rs.getDouble("balance")
                    );
                    
                    return new Vehicle(
                        rs.getInt("vehicle_id"),
                        rs.getInt("seat_number"),
                        rs.getInt("bike_spot_number"),
                        owner
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du véhicule", e);
        }
        
        return null;
    }

    public List<Vehicle> findByMember(Member member) throws DAOException {
        final String SQL = "SELECT vehicle_id, seat_number, bike_spot_number FROM Vehicle WHERE owner_id = ?";
        
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, member.getId());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehicle vehicle = new Vehicle(
                        rs.getInt("vehicle_id"),
                        rs.getInt("seat_number"),
                        rs.getInt("bike_spot_number"),
                        member
                    );
                    
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des véhicules du membre", e);
        }
        
        return vehicles;
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