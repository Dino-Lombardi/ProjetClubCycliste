package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.Lombardi.pojo.Member;
import be.Lombardi.pojo.Vehicle;

public class VehicleDAO extends DAO<Vehicle> {

    public VehicleDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Vehicle vehicle) {
        final String SQL = "INSERT INTO Vehicle (seat_number, bike_spot_number, owner_id) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, vehicle.getSeatNumber());
            ps.setInt(2, vehicle.getBikeSpotNumber());
            ps.setInt(3, vehicle.getOwner().getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du véhicule", e);
        }
    }

    @Override
    public boolean delete(Vehicle vehicle) {
        final String SQL = "DELETE FROM Vehicle WHERE vehicle_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, vehicle.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du véhicule", e);
        }
    }

    @Override
    public boolean update(Vehicle vehicle) {
        final String SQL = "UPDATE Vehicle SET seat_number = ?, bike_spot_number = ?, owner_id = ? WHERE vehicle_id = ?";
        
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
    public Vehicle find(int id) {
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
                        rs.getString("name"),
                        rs.getString("firstname"),
                        rs.getString("tel"),
                        rs.getString("username"),
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

    public List<Vehicle> findByMember(Member member) {
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
            throw new DAOException("Erreur lors de la recherche des véhicules", e);
        }
        
        return vehicles;
    }

    public List<Vehicle> findVehiclesForRide(int rideId) {
        final String SQL = """
            SELECT DISTINCT v.vehicle_id, v.seat_number, v.bike_spot_number, v.owner_id,
                   p.name, p.firstname, p.tel, p.username,
                   m.balance
            FROM Vehicle v
            JOIN Inscription i ON v.vehicle_id = i.vehicle_id
            JOIN Member m ON v.owner_id = m.person_id
            JOIN Person p ON m.person_id = p.person_id
            WHERE i.ride_id = ?
            AND i.vehicle_id IS NOT NULL
            """;
        
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, rideId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Member owner = new Member(
                        rs.getInt("owner_id"),
                        rs.getString("name"),
                        rs.getString("firstname"),
                        rs.getString("tel"),
                        rs.getString("username"),
                        rs.getDouble("balance")
                    );
                    
                    Vehicle vehicle = new Vehicle(
                        rs.getInt("vehicle_id"),
                        rs.getInt("seat_number"),
                        rs.getInt("bike_spot_number"),
                        owner
                    );
                    
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des véhicules pour la sortie", e);
        }
        
        return vehicles;
    }
}