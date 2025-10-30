package be.Lombardi.dao;

import java.sql.Connection;

import be.Lombardi.pojo.Ride;

public class RideDAO extends DAO<Ride>{

	public RideDAO(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Ride obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Ride obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Ride obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Ride find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
