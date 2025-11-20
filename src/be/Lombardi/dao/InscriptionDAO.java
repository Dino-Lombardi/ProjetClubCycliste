package be.Lombardi.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.Lombardi.pojo.*;

public class InscriptionDAO extends DAO<Inscription> {

    public InscriptionDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Inscription inscription) {
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
    public boolean delete(Inscription inscription) {
        final String SQL = "DELETE FROM Inscription WHERE inscription_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, inscription.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de l'inscription", e);
        }
    }

    @Override
    public boolean update(Inscription inscription) {
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
    public Inscription find(int id) {
        final String SQL = """
            SELECT i.inscription_id, i.is_passenger, i.has_bike, i.member_id, i.ride_id, i.vehicle_id, i.bike_id
            FROM Inscription i
            WHERE i.inscription_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Inscription(
                        rs.getInt("inscription_id"),
                        rs.getBoolean("is_passenger"),
                        rs.getBoolean("has_bike"),
                        null,
                        null
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de l'inscription", e);
        }
        return null;
    }

    public List<Inscription> findByRide(int rideId) {
        final String SQL = """
            SELECT i.inscription_id, i.is_passenger, i.has_bike, i.member_id, 
                   i.vehicle_id, i.bike_id,
                   p.name as member_name, p.firstname as member_firstname,
                   v.seat_number, v.bike_spot_number, v.owner_id,
                   po.name as owner_name, po.firstname as owner_firstname, 
                   po.tel as owner_tel, po.username as owner_username,
                   mo.balance as owner_balance,
                   b.weight, b.type, b.length, b.owner_id as bike_owner_id
            FROM Inscription i
            JOIN Person p ON i.member_id = p.person_id
            LEFT JOIN Vehicle v ON i.vehicle_id = v.vehicle_id
            LEFT JOIN Member mo ON v.owner_id = mo.person_id
            LEFT JOIN Person po ON mo.person_id = po.person_id
            LEFT JOIN Bike b ON i.bike_id = b.bike_id
            WHERE i.ride_id = ?
            """;
        
        List<Inscription> inscriptions = new ArrayList<>();
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, rideId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member(
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        rs.getString("member_firstname"),
                        "", "", 0.0
                    );
                    
                    Vehicle vehicle = null;
                    if (rs.getObject("vehicle_id") != null) {
                        Member owner = new Member(
                            rs.getInt("owner_id"),
                            rs.getString("owner_name"),
                            rs.getString("owner_firstname"),
                            rs.getString("owner_tel"),
                            rs.getString("owner_username"),
                            rs.getDouble("owner_balance")
                        );
                        
                        vehicle = new Vehicle(
                            rs.getInt("vehicle_id"),
                            rs.getInt("seat_number"),
                            rs.getInt("bike_spot_number"),
                            owner
                        );
                    }
                    
                    Bike bike = null;
                    if (rs.getObject("bike_id") != null) {
                        bike = new Bike(
                            rs.getInt("bike_id"),
                            rs.getDouble("weight"),
                            rs.getString("type"),
                            rs.getDouble("length"),
                            member
                        );
                    }
                    
                    Inscription inscription = new Inscription(
                        rs.getInt("inscription_id"),
                        rs.getBoolean("is_passenger"),
                        rs.getBoolean("has_bike"),
                        member,
                        null
                    );
                    
                    inscription.setVehicle(vehicle);
                    inscription.setBike(bike);
                    
                    inscriptions.add(inscription);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des inscriptions", e);
        }
        return inscriptions;
    }
}