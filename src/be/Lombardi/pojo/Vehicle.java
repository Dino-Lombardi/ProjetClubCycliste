package be.Lombardi.pojo;

public class Vehicle {
    private int id;
    private int seatNumber;
    private int bikeSpotNumber;
    private Member owner;

    public Vehicle() {}

    public Vehicle(int id, int seatNumber, int bikeSpotNumber, Member owner) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.bikeSpotNumber = bikeSpotNumber;
        this.owner = owner;
    }

    public void validate() {
        if (seatNumber < 1) {
            throw new IllegalArgumentException("Un véhicule doit avoir au moins 1 place (le conducteur)");
        }
        
        if (bikeSpotNumber < 0) {
            throw new IllegalArgumentException("Le nombre de places vélos ne peut pas être négatif");
        }
        
        if (owner == null) {
            throw new IllegalArgumentException("Un véhicule doit avoir un propriétaire");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public int getBikeSpotNumber() {
        return bikeSpotNumber;
    }

    public void setBikeSpotNumber(int bikeSpotNumber) {
        this.bikeSpotNumber = bikeSpotNumber;
    }

    public Member getOwner() {
        return owner;
    }

    public void setOwner(Member owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        if (id == 0) return "Sélectionner...";
        return String.format("Véhicule #%d - %d places - %d places vélos", id, seatNumber, bikeSpotNumber);
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
        return Integer.hashCode(id);
    }
}