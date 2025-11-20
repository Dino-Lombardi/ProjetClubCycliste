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
    private int maxInscriptions;
    private CategoryType category;
    private List<Vehicle> vehicles;
    private Set<Inscription> inscriptions;

    public Ride() {
        this.vehicles = new ArrayList<>();
        this.inscriptions = new HashSet<>();
    }

    public Ride(int id, String startPlace, LocalDateTime startDate, double fee, Manager organizer,
                int maxInscriptions, CategoryType category) {
        this.id = id;
        this.startPlace = startPlace;
        this.startDate = startDate;
        this.fee = fee;
        this.organizer = organizer;
        this.maxInscriptions = maxInscriptions;
        this.category = category;
        this.vehicles = new ArrayList<>();
        this.inscriptions = new HashSet<>();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public Manager getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Manager organizer) {
        this.organizer = organizer;
    }

    public int getMaxInscriptions() {
        return maxInscriptions;
    }

    public void setMaxInscriptions(int maxInscriptions) {
        this.maxInscriptions = maxInscriptions;
    }

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Set<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(Set<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }

    // Méthodes métier

    /**
     * Vérifie si la date de la sortie est dépassée
     */
    public boolean isDatePassed() {
        return startDate.isBefore(LocalDateTime.now());
    }

    /**
     * Vérifie si le nombre maximum d'inscriptions est atteint
     */
    public boolean isMaxRegistrationsReached() {
        return inscriptions.size() >= maxInscriptions;
    }

    /**
     * Vérifie si un membre est déjà inscrit à cette sortie
     */
    public boolean isMemberAlreadyRegistered(Member member) {
        return inscriptions.stream()
                .anyMatch(ins -> ins.getMember() != null && ins.getMember().getId() == member.getId());
    }

    /**
     * Vérifie si un membre peut s'inscrire à cette sortie
     */
    public boolean canMemberSubscribe(Member member) {
        return !isDatePassed() &&
               !isMemberAlreadyRegistered(member) &&
               !isMaxRegistrationsReached();
    }

    /**
     * Calcule le nombre total de places passagers disponibles
     */
    public int getTotalPassengerCapacity() {
        return vehicles.stream()
                .mapToInt(v -> v.getSeatNumber() - 1) // -1 pour le conducteur
                .sum();
    }

    /**
     * Calcule le nombre de places passagers utilisées
     */
    public int getUsedPassengerSpots() {
        return (int) inscriptions.stream()
                .filter(Inscription::isPassenger)
                .count();
    }

    /**
     * Calcule le nombre de places passagers disponibles
     */
    public int getAvailablePassengerSpots() {
        return getTotalPassengerCapacity() - getUsedPassengerSpots();
    }

    /**
     * Vérifie s'il reste des places passagers disponibles
     */
    public boolean hasAvailablePassengerSpots() {
        return getAvailablePassengerSpots() > 0;
    }

    /**
     * Calcule le nombre total de places vélos disponibles
     */
    public int getTotalBikeCapacity() {
        return vehicles.stream()
                .mapToInt(Vehicle::getBikeSpotNumber)
                .sum();
    }

    /**
     * Calcule le nombre de places vélos utilisées
     */
    public int getUsedBikeSpots() {
        return (int) inscriptions.stream()
                .filter(Inscription::hasBike)
                .count();
    }

    /**
     * Calcule le nombre de places vélos disponibles
     */
    public int getAvailableBikeSpots() {
        return getTotalBikeCapacity() - getUsedBikeSpots();
    }

    /**
     * Vérifie s'il reste des places vélos disponibles
     */
    public boolean hasAvailableBikeSpots() {
        return getAvailableBikeSpots() > 0;
    }

    /**
     * Obtient le statut d'inscription de la sortie
     */
    public String getSubscriptionStatus() {
        if (isDatePassed()) {
            return "Terminée";
        } else if (isMaxRegistrationsReached()) {
            return "Complète";
        } else {
            return "Ouverte";
        }
    }

    /**
     * Obtient le statut d'inscription pour un membre spécifique
     */
    public String getSubscriptionStatusForMember(Member member) {
        if (isDatePassed()) {
            return "Terminée";
        } else if (isMemberAlreadyRegistered(member)) {
            return "Inscrit";
        } else if (isMaxRegistrationsReached()) {
            return "Complète";
        } else {
            return "Disponible";
        }
    }

    /**
     * Compte le nombre de passagers dans un véhicule spécifique
     */
    public int getUsedSeatsForVehicle(int vehicleId) {
        return (int) inscriptions.stream()
                .filter(ins -> ins.getVehicle() != null && 
                               ins.getVehicle().getId() == vehicleId && 
                               ins.isPassenger())
                .count();
    }

    /**
     * Compte le nombre de vélos dans un véhicule spécifique
     */
    public int getUsedBikeSpotsForVehicle(int vehicleId) {
        return (int) inscriptions.stream()
                .filter(ins -> ins.getVehicle() != null && 
                               ins.getVehicle().getId() == vehicleId && 
                               ins.hasBike())
                .count();
    }

    /**
     * Calcule le nombre de places passagers disponibles dans un véhicule
     */
    public int getAvailableSeatsForVehicle(Vehicle vehicle) {
        if (vehicle == null) return 0;
        int totalSeats = vehicle.getSeatNumber() - 1; // -1 pour le conducteur
        int usedSeats = getUsedSeatsForVehicle(vehicle.getId());
        return Math.max(0, totalSeats - usedSeats);
    }

    /**
     * Calcule le nombre de places vélos disponibles dans un véhicule
     */
    public int getAvailableBikeSpotsForVehicle(Vehicle vehicle) {
        if (vehicle == null) return 0;
        int totalBikeSpots = vehicle.getBikeSpotNumber();
        int usedBikeSpots = getUsedBikeSpotsForVehicle(vehicle.getId());
        return Math.max(0, totalBikeSpots - usedBikeSpots);
    }

    @Override
    public String toString() {
        return "Ride{" +
                "id=" + id +
                ", startPlace='" + startPlace + '\'' +
                ", startDate=" + startDate +
                ", fee=" + fee +
                ", organizer=" + (organizer != null ? organizer.getFirstname() + " " + organizer.getName() : "null") +
                ", maxInscriptions=" + maxInscriptions +
                ", category=" + category +
                ", inscriptions=" + inscriptions.size() +
                ", vehicles=" + vehicles.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return id == ride.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}