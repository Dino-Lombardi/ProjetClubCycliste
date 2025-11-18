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
    public boolean create(Vehicle obj) {
        final String SQL_INSERT = "INSERT INTO Vehicle (seat_number, bike_spot_number, owner_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, obj.getSeatNumber());
            ps.setInt(2, obj.getBikeSpotNumber());
            ps.setInt(3, obj.getDriver().getId());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        obj.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Vehicle obj) {
        final String SQL_DELETE = "DELETE FROM Vehicle WHERE vehicle_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Vehicle obj) {
        final String SQL_UPDATE = "UPDATE Vehicle SET seat_number = ?, bike_spot_number = ?, owner_id = ? WHERE vehicle_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(SQL_UPDATE)) {
            ps.setInt(1, obj.getSeatNumber());
            ps.setInt(2, obj.getBikeSpotNumber());
            ps.setInt(3, obj.getDriver().getId());
            ps.setInt(4, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Vehicle find(int id) {
        final String SQL_FIND = """
            SELECT v.vehicle_id, v.seat_number, v.bike_spot_number, v.owner_id,
                   p.name, p.firstname
            FROM Vehicle v
            JOIN Person p ON v.owner_id = p.person_id
            WHERE v.vehicle_id = ?
            """;
        try (PreparedStatement ps = connect.prepareStatement(SQL_FIND)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member owner = new Member(rs.getInt("owner_id"), rs.getString("name"), 
                                            rs.getString("firstname"), "", "", "", 0.0);
                    return new Vehicle(rs.getInt("vehicle_id"), rs.getInt("seat_number"), 
                                     rs.getInt("bike_spot_number"), owner);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Vehicle> findByMember(Member member) {
        final String SQL_FIND = "SELECT vehicle_id, seat_number, bike_spot_number FROM Vehicle WHERE owner_id = ?";
        List<Vehicle> vehicles = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(SQL_FIND)) {
            ps.setInt(1, member.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehicle vehicle = new Vehicle(rs.getInt("vehicle_id"), rs.getInt("seat_number"), 
                                                rs.getInt("bike_spot_number"), member);
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public boolean linkVehicleToRide(Vehicle vehicle, Ride ride) {
        final String SQL_LINK = "INSERT INTO RideVehicles (ride_id, vehicle_id) VALUES (?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(SQL_LINK)) {
            ps.setInt(1, ride.getId());
            ps.setInt(2, vehicle.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addPassengerToRideVehicle(Ride ride, Vehicle vehicle, Member passenger) {
        final String SQL_INSERT = "INSERT INTO RideVehiclePassengers (ride_id, vehicle_id, person_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, ride.getId());
            ps.setInt(2, vehicle.getId());
            ps.setInt(3, passenger.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addBikeToRideVehicle(Ride ride, Vehicle vehicle, Bike bike) {
        final String SQL_INSERT = "INSERT INTO RideVehicleBikes (ride_id, vehicle_id, bike_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, ride.getId());
            ps.setInt(2, vehicle.getId());
            ps.setInt(3, bike.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Vehicle> findVehiclesForRide(int rideId) {
        final String SQL = """
            SELECT v.vehicle_id, v.seat_number, v.bike_spot_number, v.owner_id,
                   p.name, p.firstname, p.tel
            FROM Vehicle v
            JOIN RideVehicles rv ON v.vehicle_id = rv.vehicle_id
            JOIN Person p ON v.owner_id = p.person_id
            WHERE rv.ride_id = ?
            """;
        List<Vehicle> vehicles = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, rideId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Member owner = new Member(rs.getInt("owner_id"), rs.getString("name"), 
                                            rs.getString("firstname"), rs.getString("tel"), "", "", 0.0);
                    Vehicle vehicle = new Vehicle(rs.getInt("vehicle_id"), rs.getInt("seat_number"), 
                                                rs.getInt("bike_spot_number"), owner);
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }
}