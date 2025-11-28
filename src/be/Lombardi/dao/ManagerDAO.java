package be.Lombardi.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.Lombardi.pojo.*;

public class ManagerDAO extends DAO<Manager> {

    public ManagerDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean create(Manager manager) throws DAOException {
        final String SQL_PERSON = "INSERT INTO Person (name, firstname, tel, username) VALUES (?, ?, ?, ?)";
        final String SQL_MANAGER = "INSERT INTO Manager (person_id, category) VALUES (?, ?)";
        
        try (PreparedStatement psPerson = connect.prepareStatement(SQL_PERSON, Statement.RETURN_GENERATED_KEYS)) {
            psPerson.setString(1, manager.getName());
            psPerson.setString(2, manager.getFirstname());
            psPerson.setString(3, manager.getTel());
            psPerson.setString(4, manager.getUsername());
            
            int rowsAffected = psPerson.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = psPerson.getGeneratedKeys()) {
                    if (rs.next()) {
                        int personId = rs.getInt(1);
                        manager.setId(personId);
                        
                        try (PreparedStatement psManager = connect.prepareStatement(SQL_MANAGER)) {
                            psManager.setInt(1, personId);
                            psManager.setString(2, manager.getCategory().toString());
                            return psManager.executeUpdate() > 0;
                        }
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du manager", e);
        }
    }

    @Override
    public boolean delete(Manager manager) throws DAOException {
        final String SQL = "DELETE FROM Person WHERE person_id = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, manager.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du manager", e);
        }
    }

    @Override
    public boolean update(Manager manager) throws DAOException {
        final String SQL_PERSON = """
            UPDATE Person
            SET name = ?, firstname = ?, tel = ?, username = ?
            WHERE person_id = ?
            """;
        final String SQL_MANAGER = "UPDATE Manager SET category = ? WHERE person_id = ?";
        
        try (PreparedStatement psPerson = connect.prepareStatement(SQL_PERSON)) {
            psPerson.setString(1, manager.getName());
            psPerson.setString(2, manager.getFirstname());
            psPerson.setString(3, manager.getTel());
            psPerson.setString(4, manager.getUsername());
            psPerson.setInt(5, manager.getId());
            
            int rowsAffected = psPerson.executeUpdate();
            
            if (rowsAffected > 0) {
                try (PreparedStatement psManager = connect.prepareStatement(SQL_MANAGER)) {
                    psManager.setString(1, manager.getCategory().toString());
                    psManager.setInt(2, manager.getId());
                    return psManager.executeUpdate() > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du manager", e);
        }
    }

    @Override
    public Manager find(int id) throws DAOException {
        final String SQL = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username,
                   m.category
            FROM Manager m
            JOIN Person p ON m.person_id = p.person_id
            WHERE m.person_id = ?
            """;
        
        try (PreparedStatement ps = connect.prepareStatement(SQL)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CategoryType category = null;
                    String categoryStr = getSafe(rs, "category");
                    if (!categoryStr.isEmpty()) {
                        try {
                            category = CategoryType.valueOf(categoryStr.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            // Catégorie invalide
                        }
                    }
                    
                    return new Manager(
                        rs.getInt("person_id"),
                        getSafe(rs, "name"),
                        getSafe(rs, "firstname"),
                        getSafe(rs, "tel"),
                        getSafe(rs, "username"),
                        category
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du manager", e);
        }
        
        return null;
    }

    public List<Manager> findAll() throws DAOException {
        final String SQL = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username,
                   m.category
            FROM Manager m
            JOIN Person p ON m.person_id = p.person_id
            ORDER BY p.name, p.firstname
            """;
        
        List<Manager> managers = new ArrayList<>();
        
        try (PreparedStatement ps = connect.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                CategoryType category = null;
                String categoryStr = getSafe(rs, "category");
                if (!categoryStr.isEmpty()) {
                    try {
                        category = CategoryType.valueOf(categoryStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        // Catégorie invalide
                    }
                }
                
                Manager manager = new Manager(
                    rs.getInt("person_id"),
                    getSafe(rs, "name"),
                    getSafe(rs, "firstname"),
                    getSafe(rs, "tel"),
                    getSafe(rs, "username"),
                    category
                );
                
                managers.add(manager);
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération des managers", e);
        }
        
        return managers;
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