package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.Lombardi.pojo.CategoryType;
import be.Lombardi.pojo.Inscription;
import be.Lombardi.pojo.Manager;
import be.Lombardi.pojo.Member;
import be.Lombardi.pojo.Ride;
import be.Lombardi.pojo.Vehicle;

public class RideDAO extends DAO<Ride> {

    public RideDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Ride ride) {
        final String SQL = """
            INSERT INTO Ride (start_place, start_date, fee, category, max_inscriptions, manager_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setString(1, ride.getStartPlace());
            ps.setTimestamp(2, Timestamp.valueOf(ride.getStartDate()));
            ps.setDouble(3, ride.getFee());
            ps.setString(4, ride.getCategory().toString());
            ps.setInt(5, ride.getMaxInscriptions());
            ps.setInt(6, ride.getOrganizer().getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création de la sortie", e);
        }
    }

    @Override
    public boolean delete(Ride ride) {
        final String SQL = "DELETE FROM Ride WHERE ride_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, ride.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de la sortie", e);
        }
    }

    @Override
    public boolean update(Ride ride) {
        final String SQL = """
            UPDATE Ride
            SET start_place = ?, start_date = ?, fee = ?, category = ?, max_inscriptions = ?, manager_id = ?
            WHERE ride_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setString(1, ride.getStartPlace());
            ps.setTimestamp(2, Timestamp.valueOf(ride.getStartDate()));
            ps.setDouble(3, ride.getFee());
            ps.setString(4, ride.getCategory().toString());
            ps.setInt(5, ride.getMaxInscriptions());
            ps.setInt(6, ride.getOrganizer().getId());
            ps.setInt(7, ride.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de la sortie", e);
        }
    }

    @Override
    public Ride find(int id) {
        final String SQL = """
            SELECT r.ride_id, r.start_place, r.start_date, r.fee, r.category, r.max_inscriptions, r.manager_id,
                   m.person_id as manager_person_id, p.name as manager_name, p.firstname as manager_firstname,
                   p.tel as manager_tel, p.username as manager_username,
                   m.category as manager_category
            FROM Ride r
            LEFT JOIN Manager m ON r.manager_id = m.person_id
            LEFT JOIN Person p ON m.person_id = p.person_id
            WHERE r.ride_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Manager manager = null;
                    if (rs.getInt("manager_person_id") != 0) {
                        manager = new Manager(
                            rs.getInt("manager_person_id"),
                            getSafe(rs, "manager_name"),
                            getSafe(rs, "manager_firstname"),
                            getSafe(rs, "manager_tel"),
                            getSafe(rs, "manager_username"),
                            CategoryType.valueOf(getSafe(rs, "manager_category").toUpperCase())
                        );
                    }
                    
                    return new Ride(
                        rs.getInt("ride_id"),
                        getSafe(rs, "start_place"),
                        rs.getTimestamp("start_date").toLocalDateTime(),
                        rs.getDouble("fee"),
                        manager,
                        rs.getInt("max_inscriptions"),
                        CategoryType.valueOf(getSafe(rs, "category").toUpperCase())
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de la sortie", e);
        }
        
        return null;
    }

    public List<Ride> findAll() {
        final String SQL_RIDES = """
            SELECT r.ride_id, r.start_place, r.start_date, r.fee, r.category, r.max_inscriptions, r.manager_id,
                   m.person_id as manager_person_id, p.name as manager_name, p.firstname as manager_firstname,
                   p.tel as manager_tel, p.username as manager_username,
                   m.category as manager_category
            FROM Ride r
            LEFT JOIN Manager m ON r.manager_id = m.person_id
            LEFT JOIN Person p ON m.person_id = p.person_id
            ORDER BY r.start_date DESC
            """;

        try {
            List<Ride> rides = new ArrayList<>();
            Map<Integer, Ride> ridesById = new HashMap<>();
            
            try (PreparedStatement st = connect.prepareStatement(SQL_RIDES)) {
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        Manager manager = null;
                        if (rs.getInt("manager_person_id") != 0) {
                            manager = new Manager(
                                rs.getInt("manager_person_id"),
                                getSafe(rs, "manager_name"),
                                getSafe(rs, "manager_firstname"),
                                getSafe(rs, "manager_tel"),
                                getSafe(rs, "manager_username"),
                                CategoryType.valueOf(getSafe(rs, "manager_category").toUpperCase())
                            );
                        }
                        
                        Ride ride = new Ride(
                            rs.getInt("ride_id"),
                            getSafe(rs, "start_place"),
                            rs.getTimestamp("start_date").toLocalDateTime(),
                            rs.getDouble("fee"),
                            manager,
                            rs.getInt("max_inscriptions"),
                            CategoryType.valueOf(getSafe(rs, "category").toUpperCase())
                        );
                        
                        rides.add(ride);
                        ridesById.put(ride.getId(), ride);
                    }
                }
            }

            if (rides.isEmpty()) {
                return rides;
            }

            loadVehiclesForRides(rides, ridesById);
            loadInscriptionsForRides(rides, ridesById);

            return rides;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de toutes les sorties", e);
        }
    }

    public List<Ride> findByMemberCategories(Set<CategoryType> memberCategories) {
        if (memberCategories == null || memberCategories.isEmpty()) {
            return new ArrayList<>();
        }

        final String SQL_RIDES = """
            SELECT r.ride_id, r.start_place, r.start_date, r.fee, r.category, r.max_inscriptions, r.manager_id,
                   m.person_id as manager_person_id, p.name as manager_name, p.firstname as manager_firstname,
                   p.tel as manager_tel, p.username as manager_username,
                   m.category as manager_category
            FROM Ride r
            LEFT JOIN Manager m ON r.manager_id = m.person_id
            LEFT JOIN Person p ON m.person_id = p.person_id
            WHERE r.category IN (%s)
            ORDER BY r.start_date DESC
            """;

        try {
            List<Ride> rides = new ArrayList<>();
            Map<Integer, Ride> ridesById = new HashMap<>();
            
            String placeholders = String.join(",", Collections.nCopies(memberCategories.size(), "?"));
            String ridesSQL = String.format(SQL_RIDES, placeholders);
            
            try (PreparedStatement st = connect.prepareStatement(ridesSQL)) {
                int index = 1;
                for (CategoryType category : memberCategories) {
                    st.setString(index++, category.toString());
                }
                
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        Manager manager = null;
                        if (rs.getInt("manager_person_id") != 0) {
                            manager = new Manager(
                                rs.getInt("manager_person_id"),
                                getSafe(rs, "manager_name"),
                                getSafe(rs, "manager_firstname"),
                                getSafe(rs, "manager_tel"),
                                getSafe(rs, "manager_username"),
                                CategoryType.valueOf(getSafe(rs, "manager_category").toUpperCase())
                            );
                        }
                        
                        Ride ride = new Ride(
                            rs.getInt("ride_id"),
                            getSafe(rs, "start_place"),
                            rs.getTimestamp("start_date").toLocalDateTime(),
                            rs.getDouble("fee"),
                            manager,
                            rs.getInt("max_inscriptions"),
                            CategoryType.valueOf(getSafe(rs, "category").toUpperCase())
                        );
                        
                        rides.add(ride);
                        ridesById.put(ride.getId(), ride);
                    }
                }
            }

            if (rides.isEmpty()) {
                return rides;
            }

            loadVehiclesForRides(rides, ridesById);
            loadInscriptionsForRides(rides, ridesById);

            return rides;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération des sorties par catégories", e);
        }
    }

    private void loadVehiclesForRides(List<Ride> rides, Map<Integer, Ride> ridesById) {
        if (rides.isEmpty()) return;

        final String SQL_VEHICLES = """
            SELECT DISTINCT i.ride_id, v.vehicle_id, v.seat_number, v.bike_spot_number, v.owner_id,
                   p.name as owner_name, p.firstname as owner_firstname, p.tel as owner_tel,
                   p.username as owner_username, m.balance as owner_balance
            FROM Inscription i
            JOIN Vehicle v ON i.vehicle_id = v.vehicle_id
            JOIN Member m ON v.owner_id = m.person_id
            JOIN Person p ON m.person_id = p.person_id
            WHERE i.ride_id IN (%s)
            AND i.vehicle_id IS NOT NULL
            ORDER BY i.ride_id, v.vehicle_id
            """;

        try {
            String placeholders = String.join(",", Collections.nCopies(rides.size(), "?"));
            String vehiclesSQL = String.format(SQL_VEHICLES, placeholders);
            
            try (PreparedStatement st = connect.prepareStatement(vehiclesSQL)) {
                int index = 1;
                for (Ride ride : rides) {
                    st.setInt(index++, ride.getId());
                }
                
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        int rideId = rs.getInt("ride_id");
                        Ride ride = ridesById.get(rideId);
                        
                        if (ride != null) {
                            Member owner = new Member(
                                rs.getInt("owner_id"),
                                getSafe(rs, "owner_name"),
                                getSafe(rs, "owner_firstname"),
                                getSafe(rs, "owner_tel"),
                                getSafe(rs, "owner_username"),
                                rs.getDouble("owner_balance")
                            );
                            
                            Vehicle vehicle = new Vehicle(
                                rs.getInt("vehicle_id"),
                                rs.getInt("seat_number"),
                                rs.getInt("bike_spot_number"),
                                owner
                            );
                            
                            if (!ride.getVehicles().contains(vehicle)) {
                                ride.getVehicles().add(vehicle);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors du chargement des véhicules", e);
        }
    }

    private void loadInscriptionsForRides(List<Ride> rides, Map<Integer, Ride> ridesById) {
        if (rides.isEmpty()) return;

        final String SQL_INSCRIPTIONS = """
            SELECT i.inscription_id, i.is_passenger, i.has_bike, i.member_id, i.ride_id,
                   p.name as member_name, p.firstname as member_firstname
            FROM Inscription i
            JOIN Person p ON i.member_id = p.person_id
            WHERE i.ride_id IN (%s)
            ORDER BY i.ride_id, i.inscription_id
            """;

        try {
            String placeholders = String.join(",", Collections.nCopies(rides.size(), "?"));
            String inscriptionsSQL = String.format(SQL_INSCRIPTIONS, placeholders);
            
            try (PreparedStatement st = connect.prepareStatement(inscriptionsSQL)) {
                int index = 1;
                for (Ride ride : rides) {
                    st.setInt(index++, ride.getId());
                }
                
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        int rideId = rs.getInt("ride_id");
                        Ride ride = ridesById.get(rideId);
                        
                        if (ride != null) {
                            Member member = new Member(
                                rs.getInt("member_id"),
                                getSafe(rs, "member_name"),
                                getSafe(rs, "member_firstname"),
                                "", "", 0.0
                            );
                            
                            Inscription inscription = new Inscription(
                                rs.getInt("inscription_id"),
                                rs.getBoolean("is_passenger"),
                                rs.getBoolean("has_bike"),
                                member,
                                ride
                            );
                            
                            ride.getInscriptions().add(inscription);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Erreur lors du chargement des inscriptions", e);
        }
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