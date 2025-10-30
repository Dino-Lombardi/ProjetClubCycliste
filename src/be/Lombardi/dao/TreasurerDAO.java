package be.Lombardi.dao;

import java.sql.Connection;

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
		// TODO Auto-generated method stub
		return null;
	}

}
