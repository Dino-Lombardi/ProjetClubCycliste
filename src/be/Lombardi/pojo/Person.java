package be.Lombardi.pojo;

public abstract class Person {
    private int id;
    private String name;
    private String firstname;
    private String tel;
    private String username;

    public Person() {}

    public Person(int id, String name, String firstname, String tel, String username) {
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.tel = tel;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return getFirstname() + " " + getName();
    }

    @Override
    public String toString() {
        return "Person{" +
               "id=" + getId() +
               ", name='" + getName() + '\'' +
               ", firstname='" + getFirstname() + '\'' +
               ", tel='" + getTel() + '\'' +
               ", username='" + getUsername() + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return getId() == person.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}