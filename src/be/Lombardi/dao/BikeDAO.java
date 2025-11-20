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
    public boolean create(Bike bike) {
        final String SQL = "INSERT INTO Bike (weight, type, length, owner_id) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setDouble(1, bike.getWeight());
            ps.setString(2, bike.getType());
            ps.setDouble(3, bike.getLength());
            ps.setInt(4, bike.getOwner().getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du vélo", e);
        }
    }

    @Override
    public boolean delete(Bike bike) {
        final String SQL = "DELETE FROM Bike WHERE bike_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, bike.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du vélo", e);
        }
    }

    @Override
    public boolean update(Bike bike) {
        final String SQL = "UPDATE Bike SET weight = ?, type = ?, length = ?, owner_id = ? WHERE bike_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setDouble(1, bike.getWeight());
            ps.setString(2, bike.getType());
            ps.setDouble(3, bike.getLength());
            ps.setInt(4, bike.getOwner().getId());
            ps.setInt(5, bike.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du vélo", e);
        }
    }

    @Override
    public Bike find(int id) {
        final String SQL = """
            SELECT b.bike_id, b.weight, b.type, b.length, b.owner_id,
                   p.name, p.firstname, p.tel, p.username,
                   m.balance
            FROM Bike b
            JOIN Member m ON b.owner_id = m.person_id
            JOIN Person p ON m.person_id = p.person_id
            WHERE b.bike_id = ?
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
                    
                    return new Bike(
                        rs.getInt("bike_id"),
                        rs.getDouble("weight"),
                        rs.getString("type"),
                        rs.getDouble("length"),
                        owner
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du vélo", e);
        }
        
        return null;
    }

    public List<Bike> findByMember(Member member) {
        final String SQL = "SELECT bike_id, weight, type, length FROM Bike WHERE owner_id = ? ORDER BY type";
        List<Bike> bikes = new ArrayList<>();
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
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
            throw new DAOException("Erreur lors de la recherche des vélos", e);
        }
        
        return bikes;
    }
}