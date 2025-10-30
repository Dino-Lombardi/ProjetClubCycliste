package be.Lombardi.pojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Member extends Person{
	private double balance;
    private Set<CategoryType> categories;
    private List<Vehicle> vehicles;
    private List<Bike> bikes;
    private List<Inscription> inscriptions;
	
	public double getBalance() {
		return balance;
	}
	public Set<CategoryType> getCategories() {
		return categories;
	}
	
	public List<Vehicle> getVehicles() {
		return vehicles;
	}
	public List<Bike> getBikes() {
		return bikes;
	}
	public List<Inscription> getInscriptions() {
		return inscriptions;
	}
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public void setCategories(Set<CategoryType> categories) {
		this.categories = categories;
	}
	
	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}
	public void setBikes(List<Bike> bikes) {
		this.bikes = bikes;
	}
	public void setInscriptions(List<Inscription> inscriptions) {
		this.inscriptions = inscriptions;
	}	
	
	public void addCategory(CategoryType category) {
	    categories.add(category);
	}
	
	public void removeCategory(CategoryType category) {
		categories.remove(category);
	}
	
	public Member() {
		super();
		categories = new HashSet<>();
        vehicles = new ArrayList<>();
        bikes = new ArrayList<>();
        inscriptions = new ArrayList<>();
	}
	public Member(int id, String name, String firstname, String tel, String password, double balance, Set<CategoryType> categories) {
		super(id, name, firstname, tel, password);
        this.balance = balance;
        this.categories = categories != null ? categories : new HashSet<>();
        this.vehicles = new ArrayList<>();
        this.bikes = new ArrayList<>();
        this.inscriptions = new ArrayList<>();
	}
	
	
	
}
