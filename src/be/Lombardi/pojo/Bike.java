package be.Lombardi.pojo;

public class Bike {
    private int id;
    private double weight;
    private String type;
    private double length;
    private Member owner;

    public Bike() {}

    public Bike(int id, double weight, String type, double length, Member owner) {
        this.id = id;
        this.weight = weight;
        this.type = type;
        this.length = length;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
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
        return getType() + " (" + getWeight() + "kg)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Bike bike = (Bike) obj;
        return getId() == bike.getId() &&
               Double.compare(bike.getWeight(), getWeight()) == 0 &&
               Double.compare(bike.getLength(), getLength()) == 0 &&
               (getType() != null ? getType().equals(bike.getType()) : bike.getType() == null);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(getId());
        result = 31 * result + Double.hashCode(getWeight());
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + Double.hashCode(getLength());
        return result;
    }
}