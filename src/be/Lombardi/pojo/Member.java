package be.Lombardi.pojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Member extends Person{
	private double balance;
    private Set<CategoryType> categories = new HashSet<>();;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Bike> bikes = new ArrayList<>();
    private List<Inscription> inscriptions = new ArrayList<>();
	
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
		
	}
	public Member(int id, String name, String firstname, String tel, String username, String password, double balance) {
		super(id, name, firstname, tel, username, password);
        this.balance = balance;
	}
	
	
	@Override
	public String toString() {
		return super.toString() + " " + balance + " " + categories;
	}

	
	
}
