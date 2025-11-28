package be.Lombardi.pojo;

public class Treasurer extends Person {

    public Treasurer() {
        super();
    }

    public Treasurer(int id, String name, String firstname, String tel, String username) {
        super(id, name, firstname, tel, username);
    }

    @Override
    public String toString() {
        return "Treasurer{" +
               "id=" + getId() +
               ", name='" + getName() + '\'' +
               ", firstname='" + getFirstname() + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}