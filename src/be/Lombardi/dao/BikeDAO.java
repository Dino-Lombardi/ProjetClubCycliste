package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.Lombardi.pojo.Bike;
import be.Lombardi.pojo.Member;

public class BikeDAO extends DAO<Bike> {

    public BikeDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Bike obj) {
        final String SQL_INSERT = """
            INSERT INTO Bike (weight, type, length, owner_id)
            VALUES (?, ?, ?, ?)
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL_INSERT)) {
            ps.setDouble(1, obj.getWeight());
            ps.setString(2, obj.getType());
            ps.setDouble(3, obj.getLength());
            ps.setInt(4, obj.getMember().getId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Bike obj) {
        final String SQL_DELETE = "DELETE FROM Bike WHERE bike_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, obj.getId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Bike obj) {
        final String SQL_UPDATE = """
            UPDATE Bike 
            SET weight = ?, type = ?, length = ?, owner_id = ?
            WHERE bike_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL_UPDATE)) {
            ps.setDouble(1, obj.getWeight());
            ps.setString(2, obj.getType());
            ps.setDouble(3, obj.getLength());
            ps.setInt(4, obj.getMember().getId());
            ps.setInt(5, obj.getId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Bike find(int id) {
        final String SQL_FIND = """
            SELECT b.bike_id, b.weight, b.type, b.length, b.owner_id,
                   p.name, p.firstname
            FROM Bike b
            JOIN Person p ON b.owner_id = p.person_id
            WHERE b.bike_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL_FIND)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Créer le propriétaire (Member)
                    Member owner = new Member(
                        rs.getInt("owner_id"),
                        rs.getString("name"),
                        rs.getString("firstname"),
                        "", "", "", 0.0  // infos minimales
                    );
                    
                    // Créer le vélo
                    Bike bike = new Bike(
                        rs.getInt("bike_id"),
                        rs.getDouble("weight"),
                        rs.getString("type"),
                        rs.getDouble("length"),
                        owner
                    );
                    
                    return bike;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Trouve tous les vélos d'un membre
     */
    public List<Bike> findByMember(Member member) {
        final String SQL_FIND_BY_MEMBER = """
            SELECT bike_id, weight, type, length
            FROM Bike
            WHERE owner_id = ?
            ORDER BY type
            """;
        
        List<Bike> bikes = new ArrayList<>();
        
        try (PreparedStatement ps = connect.prepareStatement(SQL_FIND_BY_MEMBER)) {
            ps.setInt(1, member.getId());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bike bike = new Bike(
                        rs.getInt("bike_id"),
                        rs.getDouble("weight"),
                        rs.getString("type"),
                        rs.getDouble("length"),
                        member
                    );
                    bikes.add(bike);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bikes;
    }

    /**
     * Trouve un vélo disponible pour un membre (premier trouvé)
     */
    public Bike findAvailableBikeForMember(Member member) {
        List<Bike> memberBikes = findByMember(member);
        return memberBikes.isEmpty() ? null : memberBikes.get(0);
    }
}