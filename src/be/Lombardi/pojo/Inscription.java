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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPassenger() {
        return isPassenger;
    }

    public void setIsPassenger(boolean passenger) {
        isPassenger = passenger;
    }

    public boolean hasBike() {
        return hasBike;
    }

    public void setHasBike(boolean hasBike) {
        this.hasBike = hasBike;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
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
               "id=" + getId() +
               ", member=" + (getMember() != null ? getMember().getId() : "null") +
               ", ride=" + (getRide() != null ? getRide().getId() : "null") +
               ", vehicle=" + (getVehicle() != null ? getVehicle().getId() : "null") +
               ", bike=" + (getBike() != null ? getBike().getId() : "null") +
               ", isPassenger=" + isPassenger() +
               ", hasBike=" + hasBike() +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Inscription that = (Inscription) obj;
        return getId() == that.getId() &&
               isPassenger() == that.isPassenger() &&
               hasBike() == that.hasBike();
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(getId());
        result = 31 * result + (isPassenger() ? 1 : 0);
        result = 31 * result + (hasBike() ? 1 : 0);
        return result;
    }
}