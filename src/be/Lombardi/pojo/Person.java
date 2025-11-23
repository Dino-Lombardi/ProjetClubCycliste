package be.Lombardi.pojo;

import java.util.regex.Pattern;

public abstract class Person {
    private int id;
    private String name;
    private String firstname;
    private String tel;
    private String username;
    private String password;

    public Person() {}

    public Person(int id, String name, String firstname, String tel, String username, String password) {
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.tel = tel;
        this.username = username;
		this.password = password;
	}
    
    public Person(int id, String name, String firstname, String tel, String username) {
		this.id = id;
    	this.name = name;
		this.firstname = firstname;
		this.tel = tel;
		this.username = username;
    }

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        
        if (firstname == null || firstname.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        
        if (tel == null || tel.trim().isEmpty()) {
            throw new IllegalArgumentException("Le téléphone est obligatoire");
        }
        
        if (!Pattern.matches("^[+]?[0-9]{1,3}?[\\s.-]?[(]?[0-9]{1,4}[)]?[\\s.-]?[0-9]{1,4}[\\s.-]?[0-9]{1,9}$", tel.trim())) {
            throw new IllegalArgumentException(
                "Le format du téléphone est invalide. " +
                "Exemples valides: +32123456789, 0123456789, +33 1 23 45 67 89"
            );
        }
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }
        
        if (username.trim().length() < 3) {
            throw new IllegalArgumentException("Le nom d'utilisateur doit contenir au moins 3 caractères");
        }
        
        if (password.trim().isEmpty()) {
			throw new IllegalArgumentException("Le mot de passe est obligatoire");
		}
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
    
    public String getPassword() {
		return password;
	}
    
    public void setPassword(String password) {
    	this.password = password;
    }

    public String getFullName() {
        return firstname + " " + name;
    }

    @Override
    public String toString() {
        return "Person{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", firstname='" + firstname + '\'' +
               ", tel='" + tel + '\'' +
               ", username='" + username + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}