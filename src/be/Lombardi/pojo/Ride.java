package be.Lombardi.pojo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Ride {
    private int id;
    private String startPlace;
    private LocalDateTime startDate;
    private double fee;
    private Manager organizer;
    private int maxInscriptions;
    private CategoryType category;
    private Set<Inscription> inscriptions;

    public Ride() {
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
        this.inscriptions = new HashSet<>();
    }

    public void validate() {
        if (startPlace == null || startPlace.trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de départ est obligatoire");
        }
        
        if (startDate == null) {
            throw new IllegalArgumentException("La date de départ est obligatoire");
        }
        
        if (fee < 0) {
            throw new IllegalArgumentException("Le forfait ne peut pas être négatif");
        }
        
        if (organizer == null) {
            throw new IllegalArgumentException("Une sortie doit avoir un organisateur");
        }
        
        if (maxInscriptions <= 0) {
            throw new IllegalArgumentException("Le nombre maximum d'inscriptions doit être supérieur à 0");
        }
        
        if (category == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire");
        }
    }

    public void validateInscription(Inscription inscription) {
        if (inscription == null) {
            throw new IllegalArgumentException("L'inscription ne peut pas être null");
        }
        
        if (isDatePassed()) {
            throw new IllegalStateException("La date de la sortie est dépassée");
        }
        
        if (isMaxRegistrationsReached()) {
            throw new IllegalStateException("Le nombre maximum d'inscriptions est atteint");
        }
        
        if (isMemberAlreadyRegistered(inscription.getMember())) {
            throw new IllegalStateException("Vous êtes déjà inscrit à cette sortie");
        }
        
        if (!inscription.isPassenger()) {
            Vehicle vehicle = inscription.getVehicle();
            if (vehicle == null) {
                throw new IllegalArgumentException("Un conducteur doit proposer un véhicule");
            }
            
            if (vehicle.getId() == 0) {
                throw new IllegalArgumentException("Le véhicule doit être créé avant l'inscription");
            }
            
            // Vérifier que ce véhicule n'est pas déjà proposé pour cette sortie
            boolean vehicleAlreadyOffered = inscriptions.stream()
                .anyMatch(i -> i.getVehicle() != null 
                    && i.getVehicle().getId() == vehicle.getId());
            
            if (vehicleAlreadyOffered) {
                throw new IllegalStateException("Ce véhicule est déjà proposé pour cette sortie");
            }
            
            if (inscription.hasBike()) {
                if (vehicle.getBikeSpotNumber() <= 0) {
                    throw new IllegalStateException("Ce véhicule n'a pas de places pour les vélos");
                }
            }
        }
        
        if (inscription.isPassenger()) {
            Vehicle vehicle = inscription.getVehicle();
            if (vehicle == null) {
                throw new IllegalArgumentException("Vous devez sélectionner un véhicule");
            }
            
            if (vehicle.getId() == 0) {
                throw new IllegalArgumentException("Le véhicule sélectionné n'existe pas");
            }
            
            int availableSeats = getAvailableSeatsForVehicle(vehicle);
            if (availableSeats <= 0) {
                throw new IllegalStateException("Plus de places disponibles dans ce véhicule");
            }
            
            if (inscription.hasBike()) {
                int availableBikeSpots = getAvailableBikeSpotsForVehicle(vehicle);
                if (availableBikeSpots <= 0) {
                    throw new IllegalStateException("Plus de places vélos disponibles dans ce véhicule");
                }
            }
        }
    }

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

    public Set<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(Set<Inscription> inscriptions) {
        this.inscriptions = inscriptions != null ? inscriptions : new HashSet<>();
    }

    public java.util.List<Vehicle> getVehicles() {
        return inscriptions.stream()
            .map(Inscription::getVehicle)
            .filter(Objects::nonNull)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
    }

    public boolean isDatePassed() {
        return startDate != null && startDate.isBefore(LocalDateTime.now());
    }

    public boolean isMaxRegistrationsReached() {
        return inscriptions.size() >= maxInscriptions;
    }

    public boolean isMemberAlreadyRegistered(Member member) {
        if (member == null) return false;
        return inscriptions.stream()
                .anyMatch(ins -> ins.getMember() != null && ins.getMember().getId() == member.getId());
    }

    public boolean canMemberSubscribe(Member member) {
        return !isDatePassed() &&
               !isMemberAlreadyRegistered(member) &&
               !isMaxRegistrationsReached();
    }

    public int getTotalPassengerCapacity() {
        return getVehicles().stream()
                .mapToInt(v -> Math.max(0, v.getSeatNumber() - 1))
                .sum();
    }

    public int getUsedPassengerSpots() {
        return (int) inscriptions.stream()
                .filter(Inscription::isPassenger)
                .count();
    }

    public int getAvailablePassengerSpots() {
        return Math.max(0, getTotalPassengerCapacity() - getUsedPassengerSpots());
    }

    public boolean hasAvailablePassengerSpots() {
        return getAvailablePassengerSpots() > 0;
    }

    public int getTotalBikeCapacity() {
        return getVehicles().stream()
                .mapToInt(Vehicle::getBikeSpotNumber)
                .sum();
    }

    public int getUsedBikeSpots() {
        return (int) inscriptions.stream()
                .filter(Inscription::hasBike)
                .count();
    }

    public int getAvailableBikeSpots() {
        return Math.max(0, getTotalBikeCapacity() - getUsedBikeSpots());
    }

    public boolean hasAvailableBikeSpots() {
        return getAvailableBikeSpots() > 0;
    }

    public String getSubscriptionStatus() {
        if (isDatePassed()) {
            return "Terminée";
        } else if (isMaxRegistrationsReached()) {
            return "Complète";
        } else {
            return "Ouverte";
        }
    }

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

    public int getUsedSeatsForVehicle(int vehicleId) {
        return (int) inscriptions.stream()
                .filter(ins -> ins.getVehicle() != null && 
                               ins.getVehicle().getId() == vehicleId && 
                               ins.isPassenger())
                .count();
    }

    public int getUsedBikeSpotsForVehicle(int vehicleId) {
        return (int) inscriptions.stream()
                .filter(ins -> ins.getVehicle() != null && 
                               ins.getVehicle().getId() == vehicleId && 
                               ins.hasBike())
                .count();
    }

    public int getAvailableSeatsForVehicle(Vehicle vehicle) {
        if (vehicle == null) return 0;
        int totalSeats = Math.max(0, vehicle.getSeatNumber() - 1);
        int usedSeats = getUsedSeatsForVehicle(vehicle.getId());
        return Math.max(0, totalSeats - usedSeats);
    }

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
                ", vehicles=" + getVehicles().size() +
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