package be.Lombardi.daofactory;

import java.sql.Connection;

import be.Lombardi.dao.ClubConnection;
import be.Lombardi.dao.DAO;
import be.Lombardi.pojo.Bike;
import be.Lombardi.pojo.Inscription;
import be.Lombardi.pojo.Manager;
import be.Lombardi.pojo.Member;
import be.Lombardi.pojo.Person;
import be.Lombardi.pojo.Ride;
import be.Lombardi.pojo.Treasurer;
import be.Lombardi.pojo.Vehicle;
import be.Lombardi.dao.*;

public class DAOFactory extends AbstractDAOFactory {
	protected static final Connection conn = ClubConnection.getInstance();

	public DAO<Person> getPersonDAO() {
		
		return new PersonDAO(conn);
	}

	public DAO<Member> getMemberDAO() {
		
		return new MemberDAO(conn);
	}

	public DAO<Manager> getManagerDAO() {
		
		return new ManagerDAO(conn);
	}

	public DAO<Treasurer> getTreasurerDAO() {
		
		return new TreasurerDAO(conn);
	}

	public DAO<Vehicle> getVehicleDAO() {
		
		return new VehicleDAO(conn);
	}

	public DAO<Ride> getRideDAO() {
		
		return new RideDAO(conn);
	}

	public DAO<Inscription> getInscriptionDAO() {
		
		return new InscriptionDAO(conn);
	}

	public DAO<Bike> getBikeDAO() {
		
		return new BikeDAO(conn);
	}

	
}
