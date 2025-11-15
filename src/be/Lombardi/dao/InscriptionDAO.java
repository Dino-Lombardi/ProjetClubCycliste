package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.Lombardi.daofactory.DAOFactory;
import be.Lombardi.pojo.Inscription;
import be.Lombardi.pojo.Member;
import be.Lombardi.pojo.Ride;

public class InscriptionDAO extends DAO<Inscription>{

	public InscriptionDAO(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Inscription obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Inscription obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Inscription obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Inscription find(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Inscription> findall(Member member){
		final String SQL_Inscriptions = """
				SELECT * FROM Inscription 
				WHERE member_id = ?
				""";
		
		List<Inscription> inscriptions = new ArrayList<>();
		
		try (PreparedStatement ps = connect.prepareStatement(SQL_Inscriptions)) {
	        ps.setInt(1, member.getId());

	        try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	inscriptions.add(
						new Inscription(
							rs.getInt("inscription_id"),
							rs.getBoolean("is_passenger"),
							rs.getBoolean("has_bike"),
							member,
							new Ride())              			
                		);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return inscriptions;
	}			
}
