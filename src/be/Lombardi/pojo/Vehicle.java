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
        if (getId() == 0) return "Sélectionner...";
        return "Vehicle{" +
               "id=" + getId() +
               ", seatNumber=" + getSeatNumber() +
               ", bikeSpotNumber=" + getBikeSpotNumber() +
               ", owner=" + (getOwner() != null ? getOwner().getId() : "null") +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return getId() == vehicle.getId() &&
               getSeatNumber() == vehicle.getSeatNumber() &&
               getBikeSpotNumber() == vehicle.getBikeSpotNumber();
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(getId());
        result = 31 * result + Integer.hashCode(getSeatNumber());
        result = 31 * result + Integer.hashCode(getBikeSpotNumber());
        return result;
    }
}