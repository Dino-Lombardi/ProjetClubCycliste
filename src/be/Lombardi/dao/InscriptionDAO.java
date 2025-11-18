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
    public boolean create(Inscription obj) {
        final String SQL_INSERT = "INSERT INTO Inscription (is_passenger, has_bike, member_id, ride_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, obj.isPassenger());
            ps.setBoolean(2, obj.hasBike());
            ps.setInt(3, obj.getMember().getId());
            ps.setInt(4, obj.getRide().getId());
            
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
    public boolean delete(Inscription obj) {
        final String SQL_DELETE = "DELETE FROM Inscription WHERE inscription_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Inscription obj) {
        final String SQL_UPDATE = "UPDATE Inscription SET is_passenger = ?, has_bike = ?, member_id = ?, ride_id = ? WHERE inscription_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(SQL_UPDATE)) {
            ps.setBoolean(1, obj.isPassenger());
            ps.setBoolean(2, obj.hasBike());
            ps.setInt(3, obj.getMember().getId());
            ps.setInt(4, obj.getRide().getId());
            ps.setInt(5, obj.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Inscription find(int id) {
        final String SQL_FIND = """
            SELECT i.inscription_id, i.is_passenger, i.has_bike, i.member_id, i.ride_id
            FROM Inscription i
            WHERE i.inscription_id = ?
            """;
        try (PreparedStatement ps = connect.prepareStatement(SQL_FIND)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Inscription(rs.getInt("inscription_id"), rs.getBoolean("is_passenger"), 
                                         rs.getBoolean("has_bike"), null, null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Inscription> findByMember(int memberId) {
        final String SQL = "SELECT inscription_id, is_passenger, has_bike, ride_id FROM Inscription WHERE member_id = ?";
        List<Inscription> inscriptions = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inscriptions.add(new Inscription(rs.getInt("inscription_id"), rs.getBoolean("is_passenger"), 
                                                   rs.getBoolean("has_bike"), null, null));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inscriptions;
    }

    public List<Inscription> findByRide(int rideId) {
        final String SQL = """
            SELECT i.inscription_id, i.is_passenger, i.has_bike, i.member_id,
                   p.name, p.firstname
            FROM Inscription i
            JOIN Person p ON i.member_id = p.person_id
            WHERE i.ride_id = ?
            """;
        List<Inscription> inscriptions = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, rideId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member(rs.getInt("member_id"), rs.getString("name"), 
                                             rs.getString("firstname"), "", "", "", 0.0);
                    inscriptions.add(new Inscription(rs.getInt("inscription_id"), rs.getBoolean("is_passenger"), 
                                                   rs.getBoolean("has_bike"), member, null));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inscriptions;
    }

    public boolean isMemberRegisteredToRide(int memberId, int rideId) {
        final String SQL = "SELECT COUNT(*) as count FROM Inscription WHERE member_id = ? AND ride_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, memberId);
            ps.setInt(2, rideId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}