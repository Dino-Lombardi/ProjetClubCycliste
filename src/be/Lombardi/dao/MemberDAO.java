package be.Lombardi.dao;

import java.sql.Connection;

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
		// TODO Auto-generated method stub
		return null;
	}

}
