package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import be.Lombardi.pojo.CategoryType;
import be.Lombardi.pojo.Manager;
import be.Lombardi.pojo.Member;

public class ManagerDAO extends DAO<Manager>{

	public ManagerDAO(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Manager obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Manager obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Manager obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public Manager find(int id) {
        final String SQL_MEMBER = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username, p.password,
                   m.category
            FROM Person p
            JOIN Manager m ON m.person_id = p.person_id
            WHERE p.person_id = ?
        """;

        try {
            Manager manager = null;

            // 1️: Récupération des infos de Person + Manager
            try (PreparedStatement ps = connect.prepareStatement(SQL_MEMBER)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        manager = new Manager(
                            rs.getInt("person_id"),
                            rs.getString("name"),
                            rs.getString("firstname"),
                            rs.getString("tel"),
                            rs.getString("username"),
                            rs.getString("password"),
                            CategoryType.valueOf(rs.getString("category").toUpperCase())
                        );
                    }
                }
            }
            return manager;

        } catch (SQLException e) {
        	throw new IllegalStateException("Erreur SQL lors de la récupération du manager " + id, e);
        }
    }

}
