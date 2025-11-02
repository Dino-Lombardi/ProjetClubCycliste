package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.Lombardi.pojo.CategoryType;
import be.Lombardi.pojo.Manager;
import be.Lombardi.pojo.Treasurer;

public class TreasurerDAO extends DAO<Treasurer>{

	public TreasurerDAO(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Treasurer obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Treasurer obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Treasurer obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public Treasurer find(int id) {
        final String SQL_MEMBER = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username, p.password
            FROM Person p
            JOIN Treasurer t ON t.person_id = p.person_id
            WHERE p.person_id = ?
        """;

        try {
            Treasurer treasurer = null;

            // 1️: Récupération des infos de Treasurer
            try (PreparedStatement ps = connect.prepareStatement(SQL_MEMBER)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                    	treasurer = new Treasurer(
                            rs.getInt("person_id"),
                            rs.getString("name"),
                            rs.getString("firstname"),
                            rs.getString("tel"),
                            rs.getString("username"),
                            rs.getString("password")
                        );
                    }
                }
            }
            return treasurer;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur TreasurerDAO.find(" + id + ")", e);
        }
    }

}
