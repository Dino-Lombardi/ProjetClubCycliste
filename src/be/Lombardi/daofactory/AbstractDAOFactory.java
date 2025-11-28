package be.Lombardi.daofactory;

import be.Lombardi.dao.DAO;
import be.Lombardi.pojo.Bike;
import be.Lombardi.pojo.Inscription;
import be.Lombardi.pojo.Manager;
import be.Lombardi.pojo.Member;
import be.Lombardi.pojo.Person;
import be.Lombardi.pojo.Ride;
import be.Lombardi.pojo.Treasurer;
import be.Lombardi.pojo.Vehicle;


public abstract class AbstractDAOFactory {
	public static final int DAO_FACTORY = 0;
	public static final int XML_DAO_FACTORY = 1;

	public abstract DAO<Person> getPersonDAO();

	public abstract DAO<Member> getMemberDAO();

	public abstract DAO<Manager> getManagerDAO();

	public abstract DAO<Treasurer> getTreasurerDAO();
	
	public abstract DAO<Vehicle> getVehicleDAO();
	
	public abstract DAO<Ride> getRideDAO();
	
	public abstract DAO<Inscription> getInscriptionDAO();
	
	public abstract DAO<Bike> getBikeDAO();

	public static AbstractDAOFactory getFactory(int type) {
		switch (type) {
		case DAO_FACTORY:
			return new DAOFactory();
		default:
			return null;
		}
	}
}
