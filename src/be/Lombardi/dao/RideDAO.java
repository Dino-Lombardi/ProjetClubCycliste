package be.Lombardi.dao;

import java.sql.*;
import java.util.*;

import be.Lombardi.pojo.*;

public class RideDAO extends DAO<Ride> {

    public RideDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Ride ride) throws DAOException {
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
    public boolean delete(Ride ride) throws DAOException {
        final String SQL = "DELETE FROM Ride WHERE ride_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, ride.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de la sortie", e);
        }
    }

    @Override
    public boolean update(Ride ride) throws DAOException {
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
    public Ride find(int id) throws DAOException {
        final String SQL = """
            SELECT r.ride_id, r.start_place, r.start_date, r.fee, r.category, r.max_inscriptions, r.manager_id,
                   m.person_id, m.category,
                   p.name, p.firstname, p.tel, p.username
            FROM Ride r
            LEFT JOIN Manager m ON r.manager_id = m.person_id
            LEFT JOIN Person p ON m.person_id = p.person_id
            WHERE r.ride_id = ?
            """;
        
        final String SQL_INSCRIPTIONS = """
            SELECT i.inscription_id, i.is_passenger, i.has_bike, i.member_id, i.vehicle_id, i.bike_id,
                   p.name, p.firstname, p.tel, p.username,
                   v.seat_number, v.bike_spot_number, v.owner_id,
                   p_owner.name as owner_name, p_owner.firstname as owner_firstname, 
                   p_owner.tel as owner_tel, p_owner.username as owner_username,
                   m_owner.balance as owner_balance
            FROM Inscription i
            JOIN Person p ON i.member_id = p.person_id
            LEFT JOIN Vehicle v ON i.vehicle_id = v.vehicle_id
            LEFT JOIN Member m_owner ON v.owner_id = m_owner.person_id
            LEFT JOIN Person p_owner ON m_owner.person_id = p_owner.person_id
            WHERE i.ride_id = ?
            """;
        
        try {
            Ride ride;
            
            try (PreparedStatement st = connect.prepareStatement(SQL)) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    ride = buildRideFromResultSet(rs);
                }
            }
            
            try (PreparedStatement st = connect.prepareStatement(SQL_INSCRIPTIONS)) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        ride.getInscriptions().add(buildFullInscriptionFromResultSet(rs, ride));
                    }
                }
            }
            
            return ride;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de la sortie", e);
        }
    }

    public List<Ride> findAll() throws DAOException {
        return findRidesByCriteria(null);
    }

    public List<Ride> findByMemberCategories(Set<CategoryType> memberCategories) throws DAOException {
        if (memberCategories == null || memberCategories.isEmpty()) {
            return new ArrayList<>();
        }
        return findRidesByCriteria(memberCategories);
    }

    // Méthode générique pour raccourcir le code : gère findAll() et findByMemberCategories()
    private List<Ride> findRidesByCriteria(Set<CategoryType> categories) throws DAOException {
        // Construction dynamique de la clause WHERE selon les catégories
        String whereClause = (categories != null && !categories.isEmpty()) 
            ? "WHERE r.category IN (" + String.join(",", Collections.nCopies(categories.size(), "?")) + ")"
            : "";
        
        String sql = String.format("""
            SELECT r.ride_id, r.start_place, r.start_date, r.fee, r.category, r.max_inscriptions, r.manager_id,
                   m.person_id, m.category,
                   p.name, p.firstname, p.tel, p.username
            FROM Ride r
            LEFT JOIN Manager m ON r.manager_id = m.person_id
            LEFT JOIN Person p ON m.person_id = p.person_id
            %s
            ORDER BY r.start_date DESC
            """, whereClause);
        
        try {
            List<Ride> rides = new ArrayList<>();
            
            try (PreparedStatement st = connect.prepareStatement(sql)) {
                // Paramètres dynamiques selon les catégories
                if (categories != null && !categories.isEmpty()) {
                    int index = 1;
                    for (CategoryType category : categories) {
                        st.setString(index++, category.toString());
                    }
                }
                
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        rides.add(buildRideFromResultSet(rs));
                    }
                }
            }
            
            if (!rides.isEmpty()) {
                loadInscriptionsForRides(rides);
            }
            
            return rides;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération des sorties", e);
        }
    }

    // Orchestre le chargement des inscriptions (qui contiennent les vehicles)
    private void loadInscriptionsForRides(List<Ride> rides) throws DAOException {
        // LinkedHashMap : garde l'ordre d'insertion + accès O(1) par ride.getId()
        Map<Integer, Ride> ridesById = new LinkedHashMap<>();
        for (Ride ride : rides) {
            ridesById.put(ride.getId(), ride);
        }
        
        loadInscriptionsForRidesMap(ridesById);
    }

    // Batch loading : charge inscriptions de TOUTES les rides en 1 seule requête avec IN (?, ?, ...)
    // ✅ INCLUT le Vehicle dans la même requête (pas de redondance)
    private void loadInscriptionsForRidesMap(Map<Integer, Ride> ridesById) throws DAOException {
        final String SQL = """
            SELECT i.inscription_id, i.is_passenger, i.has_bike, i.member_id, i.ride_id, i.vehicle_id, i.bike_id,
                   p.name, p.firstname, p.tel, p.username,
                   v.seat_number, v.bike_spot_number, v.owner_id,
                   p_owner.name as owner_name, p_owner.firstname as owner_firstname, 
                   p_owner.tel as owner_tel, p_owner.username as owner_username,
                   m_owner.balance as owner_balance
            FROM Inscription i
            JOIN Person p ON i.member_id = p.person_id
            LEFT JOIN Vehicle v ON i.vehicle_id = v.vehicle_id
            LEFT JOIN Member m_owner ON v.owner_id = m_owner.person_id
            LEFT JOIN Person p_owner ON m_owner.person_id = p_owner.person_id
            WHERE i.ride_id IN (%s)
            ORDER BY i.ride_id, i.inscription_id
            """;
        
        String placeholders = String.join(",", Collections.nCopies(ridesById.size(), "?"));
        String finalSql = String.format(SQL, placeholders);
        
        try (PreparedStatement st = connect.prepareStatement(finalSql)) {
            int index = 1;
            for (Integer rideId : ridesById.keySet()) {
                st.setInt(index++, rideId);
            }
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int rideId = rs.getInt("ride_id");
                    Ride ride = ridesById.get(rideId);
                    if (ride != null) {
                        ride.getInscriptions().add(buildFullInscriptionFromResultSet(rs, ride));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors du chargement des inscriptions", e);
        }
    }

    // Méthode pour raccourcir le code : construit un Ride depuis ResultSet
    private Ride buildRideFromResultSet(ResultSet rs) throws SQLException {
        Manager manager = null;
        if (rs.getInt("person_id") != 0) {
            manager = buildManagerFromResultSet(rs);
        }
        
        CategoryType rideCategory = parseCategoryType(getSafe(rs, "category"));
        Timestamp timestamp = rs.getTimestamp("start_date");
        
        return new Ride(
            rs.getInt("ride_id"),
            getSafe(rs, "start_place"),
            timestamp != null ? timestamp.toLocalDateTime() : null,
            rs.getDouble("fee"),
            manager,
            rs.getInt("max_inscriptions"),
            rideCategory
        );
    }

    // Méthode pour raccourcir le code : construit un Manager depuis ResultSet
    private Manager buildManagerFromResultSet(ResultSet rs) throws SQLException {
        CategoryType category = parseCategoryType(getSafe(rs, "category"));
        
        return new Manager(
            rs.getInt("person_id"),
            getSafe(rs, "name"),
            getSafe(rs, "firstname"),
            getSafe(rs, "tel"),
            getSafe(rs, "username"),
            category
        );
    }

    // Méthode pour raccourcir le code : construit une Inscription COMPLÈTE (avec Vehicle si présent)
    private Inscription buildFullInscriptionFromResultSet(ResultSet rs, Ride ride) throws SQLException {
        Member member = new Member(
            rs.getInt("member_id"),
            getSafe(rs, "name"),
            getSafe(rs, "firstname"),
            getSafe(rs, "tel"),
            getSafe(rs, "username"),
            0.0
        );
        
        Inscription inscription = new Inscription(
            rs.getInt("inscription_id"),
            rs.getBoolean("is_passenger"),
            rs.getBoolean("has_bike"),
            member,
            ride
        );
        
        // ✅ Charger le Vehicle si présent (évite la redondance)
        int vehicleId = rs.getInt("vehicle_id");
        if (vehicleId != 0) {
            Member owner = new Member(
                rs.getInt("owner_id"),
                getSafe(rs, "owner_name"),
                getSafe(rs, "owner_firstname"),
                getSafe(rs, "owner_tel"),
                getSafe(rs, "owner_username"),
                rs.getDouble("owner_balance")
            );
            
            Vehicle vehicle = new Vehicle(
                vehicleId,
                rs.getInt("seat_number"),
                rs.getInt("bike_spot_number"),
                owner
            );
            
            inscription.setVehicle(vehicle);
        }
        
        // TODO: Charger le Bike si has_bike = true et bike_id != null
        
        return inscription;
    }

    private CategoryType parseCategoryType(String categoryStr) {
        if (categoryStr == null || categoryStr.isEmpty()) {
            return null;
        }
        try {
            return CategoryType.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
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