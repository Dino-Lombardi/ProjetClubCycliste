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

    public void validate() {
        if (weight <= 0) {
            throw new IllegalArgumentException("Le poids doit être supérieur à 0");
        }
        
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de vélo est obligatoire");
        }
        
        if (length <= 0) {
            throw new IllegalArgumentException("La longueur doit être supérieure à 0");
        }
        
        if (owner == null) {
            throw new IllegalArgumentException("Un vélo doit avoir un propriétaire");
        }
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
        if (id == 0) return "Sélectionner...";
        return type + " (" + weight + "kg, " + length + "cm)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Bike bike = (Bike) obj;
        return id == bike.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}