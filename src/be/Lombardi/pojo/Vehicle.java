package be.Lombardi.pojo;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
	private int id;
    private int seatNumber;
    private int bikeSpotNumber;
    private Member driver;
    private List<Member> passengers;
    private List<Bike> bikes;
	
    public int getId() {
		return id;
	}

	public int getSeatNumber() {
		return seatNumber;
	}

	public int getBikeSpotNumber() {
		return bikeSpotNumber;
	}

	public Member getDriver() {
		return driver;
	}

	public List<Member> getPassengers() {
		return passengers;
	}

	public List<Bike> getBikes() {
		return bikes;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}

	public void setBikeSpotNumber(int bikeSpotNumber) {
		this.bikeSpotNumber = bikeSpotNumber;
	}

	public void setDriver(Member driver) {
		this.driver = driver;
	}

	public void setPassengers(List<Member> passengers) {
		this.passengers = passengers;
	}

	public void setBikes(List<Bike> bikes) {
		this.bikes = bikes;
	}
    
    public Vehicle() {
        passengers = new ArrayList<>();
        bikes = new ArrayList<>();
    }

    public Vehicle(int id, int seatNumber, int bikeSpotNumber, Member driver) {
        this();
        this.id = id;
        this.seatNumber = seatNumber;
        this.bikeSpotNumber = bikeSpotNumber;
        this.driver = driver;
    }


	
	
	
	
	
	
}
