package be.Lombardi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import be.Lombardi.daofactory.DAOFactory;
import be.Lombardi.pojo.CategoryType;
import be.Lombardi.pojo.Member;

public class MemberDAO extends DAO<Member>{

	public MemberDAO(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Member obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Member obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Member obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public Member find(int id) {
        final String SQL_MEMBER = """
            SELECT p.person_id, p.name, p.firstname, p.tel, p.username, p.password,
                   m.balance
            FROM Person p
            JOIN Member m ON m.person_id = p.person_id
            WHERE p.person_id = ?
        """;

        final String SQL_CATEGORIES = """
            SELECT category
            FROM MemberCategory
            WHERE person_id = ?
        """;

        try {
            Member member = null;

            // 1️: Récupération des infos de Person + Member
            try (PreparedStatement ps = connect.prepareStatement(SQL_MEMBER)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        member = new Member(
                            rs.getInt("person_id"),
                            rs.getString("name"),
                            rs.getString("firstname"),
                            rs.getString("tel"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getDouble("balance")
                        );
                    }
                }
            }

            // 2️: Si le membre existe, on lui ajoute ses catégories
            if (member != null) {
                try (PreparedStatement ps = connect.prepareStatement(SQL_CATEGORIES)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String cat = rs.getString("category");
                            member.addCategory(CategoryType.valueOf(cat.toUpperCase()));
                        }
                    }
                }
            }

            return member;

        }catch (SQLException e) {
        	throw new IllegalStateException("Erreur SQL lors de la récupération du membre " + id, e);
        }
    }
}

