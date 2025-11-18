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
	private Manager organizer;
	private CategoryType category;
	private List<Vehicle> vehicles = new ArrayList<>();
	private Set<Inscription> inscriptions = new HashSet<>();; 
	private int max_inscriptions;
	
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
	
	public Manager getOrganizer() {
		return organizer;
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
	
	public int getMax_inscriptions() {
		return max_inscriptions;
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
	
	public void setOrganizer(Manager organizer) {
		this.organizer = organizer;
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
	
	public void setMax_inscriptions(int max_inscriptions) {
		this.max_inscriptions = max_inscriptions;
	}

	public Ride() {
	}
	
	public Ride(int id, String startPlace, LocalDateTime startDate, double fee, Manager organizer, int max_inscriptions, CategoryType category) {
		this();
		this.id = id;
		this.startPlace = startPlace;
		this.startDate = startDate;
		this.fee = fee;
		this.organizer = organizer;
		this.max_inscriptions = max_inscriptions;
		this.category = category;
	}

	
	 // Méthodes de vérification
    public boolean canMemberSubscribe(Member member) {
        return !isDatePassed() && 
               !isMemberAlreadyRegistered(member) && 
               !isMaxRegistrationsReached();
    }
    
    public boolean isDatePassed() {
        return LocalDateTime.now().isAfter(startDate);
    }
    
    public boolean isMemberAlreadyRegistered(Member member) {
        return inscriptions.stream()
            .anyMatch(ins -> ins.getMember().getId() == member.getId());
    }
    
    public boolean isMaxRegistrationsReached() {
        return inscriptions.size() >= max_inscriptions;
    }
    
    public boolean hasAvailablePassengerSpots() {
        int totalPassengerCapacity = vehicles.stream()
            .mapToInt(v -> v.getSeatNumber() - 1) // -1 pour le conducteur
            .sum();
        long usedPassengerSpots = inscriptions.stream()
            .filter(Inscription::isPassenger)
            .count();
        return usedPassengerSpots < totalPassengerCapacity;
    }
    
    public boolean hasAvailableBikeSpots() {
        int totalBikeCapacity = vehicles.stream()
            .mapToInt(Vehicle::getBikeSpotNumber)
            .sum();
        long usedBikeSpots = inscriptions.stream()
            .filter(Inscription::hasBike)
            .count();
        return usedBikeSpots < totalBikeCapacity;
    }
    
    // Getters pour l'affichage
    public int getAvailablePassengerSpots() {
        int totalPassengerCapacity = vehicles.stream()
            .mapToInt(v -> v.getSeatNumber() - 1)
            .sum();
        long usedPassengerSpots = inscriptions.stream()
            .filter(Inscription::isPassenger)
            .count();
        return totalPassengerCapacity - (int) usedPassengerSpots;
    }
    
    public int getAvailableBikeSpots() {
        int totalBikeCapacity = vehicles.stream()
            .mapToInt(Vehicle::getBikeSpotNumber)
            .sum();
        long usedBikeSpots = inscriptions.stream()
            .filter(Inscription::hasBike)
            .count();
        return totalBikeCapacity - (int) usedBikeSpots;
    }
    
    public String getSubscriptionStatus() {
        if (isDatePassed()) {
            return "Terminée";
        } else if (isMaxRegistrationsReached()) {
            return "Complète";
        } else {
            return "Disponible";
        }
    }
    
    public String getSubscriptionStatusForMember(Member member) {
        if (isDatePassed()) {
            return "Terminée";
        } else if (isMemberAlreadyRegistered(member)) {
            return "Déjà inscrit";
        } else if (isMaxRegistrationsReached()) {
            return "Complète";
        } else {
            return "Disponible";
        }
    }
}
