package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.Lombardi.daofactory.DAOFactory;
import be.Lombardi.pojo.Person;

public class PersonDAO extends DAO<Person>{

	public PersonDAO(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Person obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Person obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Person obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Person find(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Person login(String username, String password) {
	    final String SQL = """
	        SELECT person_id, role
	        FROM Person
	        WHERE username = ? AND password = ?
	    """;

	    try (PreparedStatement ps = connect.prepareStatement(SQL)) {
	        ps.setString(1, username);
	        ps.setString(2, password);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                int id = rs.getInt("person_id");
	                String role = rs.getString("role");

	                DAOFactory factory = new DAOFactory();

	                switch (role.toUpperCase()) {
	                    case "MEMBER":
	                        return factory.getMemberDAO().find(id);

	                    case "MANAGER":
	                        return factory.getManagerDAO().find(id);

	                    case "TREASURER":
	                        return factory.getTreasurerDAO().find(id);

	                    default:
	                    	return null;
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return null;
	}

}
