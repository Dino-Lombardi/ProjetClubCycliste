package be.Lombardi.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vehicle {
	private int id;
    private int seatNumber;
    private int bikeSpotNumber;
    private Member owner;
    private List<Member> passengers = new ArrayList<>();
    private List<Bike> bikes = new ArrayList<>();;
	
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
		return owner;
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

	public void setDriver(Member owner) {
		this.owner = owner;
	}

	public void setPassengers(List<Member> passengers) {
		this.passengers = passengers;
	}

	public void setBikes(List<Bike> bikes) {
		this.bikes = bikes;
	}
    
    public Vehicle() {
        
    }

    public Vehicle(int id, int seatNumber, int bikeSpotNumber, Member owner) {
        this();
        this.id = id;
        this.seatNumber = seatNumber;
        this.bikeSpotNumber = bikeSpotNumber;
        this.owner = owner;
    }

    
    public int getAvailablePassengerSeats() {
        return (seatNumber - 1) - passengers.size();
    }
    
    
    public int getAvailableBikeSpots() {
        return bikeSpotNumber - bikes.size();
    }
    
    
    public boolean canAcceptPassenger() {
        return getAvailablePassengerSeats() > 0;
    }
    
   
    public boolean canAcceptBike() {
        return getAvailableBikeSpots() > 0;
    }
    
    
    public boolean addPassengerSafely(Member passenger) {
        if (canAcceptPassenger() && !passengers.contains(passenger)) {
            passengers.add(passenger);
            return true;
        }
        return false;
    }
    
   
    public boolean addBikeSafely(Bike bike) {
        if (canAcceptBike() && !bikes.contains(bike)) {
            bikes.add(bike);
            return true;
        }
        return false;
    }
    
    
    public boolean removePassenger(Member passenger) {
        return passengers.remove(passenger);
    }
    
    
    public boolean removeBike(Bike bike) {
        return bikes.remove(bike);
    }
    
   
    public boolean isFull() {
        return !canAcceptPassenger() && !canAcceptBike();
    }
    
   
    public boolean isEmpty() {
        return passengers.isEmpty() && bikes.isEmpty();
    }
    

    @Override
    public String toString() {
        return String.format("Véhicule %d (%s) - %s", id, owner != null ? owner.getFirstname() : "N/A");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return id == vehicle.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

	
	
	
	
	
	
}
