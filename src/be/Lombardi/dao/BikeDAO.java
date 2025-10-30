package be.Lombardi.dao;

import java.sql.Connection;

import be.Lombardi.pojo.Bike;

public class BikeDAO extends DAO<Bike>{

	public BikeDAO(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Bike obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Bike obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Bike obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Bike find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
