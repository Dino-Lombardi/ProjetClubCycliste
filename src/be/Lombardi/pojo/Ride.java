package be.Lombardi.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ride {
	private int id;
	private String startPlace;
	private LocalDateTime startDate;
	private double fee;
	private CategoryType category;
	private List<Vehicle> vehicles;
	private Set<Inscription> inscriptions;
	
	public int getId() {
		return id;
	}
	
	public String getStartPlace() {
		return startPlace;
	}
	
	public LocalDateTime getStartDate() {
		return startDate;
	}
	
	public double getFee() {
		return fee;
	}
	
	public CategoryType getCategory() {
		return category;
	}
	
	public List<Vehicle> getVehicles() {
		return vehicles;
	}
	
	public Set<Inscription> getInscriptions() {
		return inscriptions;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setStartPlace(String startPlace) {
		this.startPlace = startPlace;
	}
	
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}
	
	public void setFee(double fee) {
		this.fee = fee;
	}
	
	public void setCategory(CategoryType category) {
		this.category = category;
	}
	
	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}
	
	public void setInscriptions(Set<Inscription> inscriptions) {
		this.inscriptions = inscriptions;
	}

	public Ride() {
		vehicles = new ArrayList<>();
		inscriptions = new HashSet<>();
	}
	
	public Ride(int id, String startPlace, LocalDateTime startDate, double fee, CategoryType category) {
		this();
		this.id = id;
		this.startPlace = startPlace;
		this.startDate = startDate;
		this.fee = fee;
		this.category = category;
	}

	
	
}
