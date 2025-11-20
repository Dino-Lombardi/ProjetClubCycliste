package be.Lombardi.pojo;

public class Inscription {
    private int id;
    private boolean isPassenger;
    private boolean hasBike;
    private Member member;
    private Ride ride;
    private Vehicle vehicle;
    private Bike bike;

    public Inscription() {}

    public Inscription(int id, boolean isPassenger, boolean hasBike, Member member, Ride ride) {
        this.id = id;
        this.isPassenger = isPassenger;
        this.hasBike = hasBike;
        this.member = member;
        this.ride = ride;
    }

    public void validate() {
        if (member == null) {
            throw new IllegalArgumentException("Une inscription doit avoir un membre");
        }
        
        if (ride == null) {
            throw new IllegalArgumentException("Une inscription doit être liée à une sortie");
        }
        
        if (!isPassenger && vehicle == null) {
            throw new IllegalArgumentException("Un conducteur doit proposer un véhicule");
        }
        
        if (isPassenger && vehicle == null) {
            throw new IllegalArgumentException("Un passager doit sélectionner un véhicule");
        }
        
        if (!isPassenger && vehicle != null && member != null && !vehicle.getOwner().equals(member)) {
            throw new IllegalArgumentException("Incohérence : membre déclaré conducteur mais véhicule d'un autre membre");
        }
        
        if (hasBike && bike == null) {
            throw new IllegalArgumentException("Si hasBike est true, un vélo doit être spécifié");
        }
        
        if (bike != null && member != null && !bike.getOwner().equals(member)) {
            throw new IllegalArgumentException("Le vélo doit appartenir au membre inscrit");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPassenger() {
        return isPassenger;
    }

    public boolean hasBike() {
        return hasBike;
    }

    public Member getMember() {
        return member;
    }

    public Ride getRide() {
        return ride;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    @Override
    public String toString() {
        return "Inscription{" +
               "id=" + id +
               ", member=" + (member != null ? member.getId() : "null") +
               ", ride=" + (ride != null ? ride.getId() : "null") +
               ", vehicle=" + (vehicle != null ? vehicle.getId() : "null") +
               ", bike=" + (bike != null ? bike.getId() : "null") +
               ", isPassenger=" + isPassenger +
               ", hasBike=" + hasBike +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Inscription that = (Inscription) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}