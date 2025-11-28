package be.Lombardi.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Member extends Person {
    private double balance;
    private LocalDate lastpaymentdate;
    private Set<CategoryType> categories;
    private List<Vehicle> vehicles;
    private List<Bike> bikes;
    private List<Inscription> inscriptions;

    public Member() {
        super();
        this.categories = new HashSet<>();
        this.vehicles = new ArrayList<>();
        this.bikes = new ArrayList<>();
        this.inscriptions = new ArrayList<>();
    }

    public Member(int id, String name, String firstname, String tel, String username, String password, double balance) {
        super(id, name, firstname, tel, username, password);
        this.balance = balance;
        this.categories = new HashSet<>();
        this.vehicles = new ArrayList<>();
        this.bikes = new ArrayList<>();
        this.inscriptions = new ArrayList<>();
    }
    
    public Member(int id, String name, String firstname, String tel, String username, double balance) {
        super(id, name, firstname, tel, username);
        this.balance = balance;
        this.categories = new HashSet<>();
        this.vehicles = new ArrayList<>();
        this.bikes = new ArrayList<>();
        this.inscriptions = new ArrayList<>();
    }
    
    public Member(int id, String name, String firstname, String tel, String username, double balance, LocalDate lastpaymentdate) {
        super(id, name, firstname, tel, username);
        this.balance = balance;
        this.lastpaymentdate = lastpaymentdate;	
        this.categories = new HashSet<>();
        this.vehicles = new ArrayList<>();
        this.bikes = new ArrayList<>();
        this.inscriptions = new ArrayList<>();
    }
    
    

    @Override
    public void validate() {
        super.validate();
        
        if (balance < 0) {
            throw new IllegalArgumentException("Le solde ne peut pas être négatif");
        }
        
        if (categories.isEmpty()) {
			throw new IllegalArgumentException("Au moins une catégorie doit être assignée au membre");
		}
    }

    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
    	if (balance < 0) {
            throw new IllegalArgumentException("Le solde ne peut pas être négatif");
    	}
        this.balance = balance;
    }
    
    public LocalDate getLastPaymentDate() {
		return lastpaymentdate;
	}	
    
    public void setLastPaymentDate(LocalDate lastpaymentdate) {
    	this.lastpaymentdate = lastpaymentdate;
    }

    public Set<CategoryType> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryType> categories) {
        this.categories = categories;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public List<Bike> getBikes() {
        return bikes;
    }

    public void setBikes(List<Bike> bikes) {
        this.bikes = bikes;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }

    public void addCategory(CategoryType category) {
        categories.add(category);
    }

    public void removeCategory(CategoryType category) {
        categories.remove(category);
    }

    public boolean hasCategory(CategoryType category) {
        return categories.contains(category);
    }

    
    // Pour vérifier si la cotisation est à jour
    public boolean isSubscriptionUpToDate() {
        if (lastpaymentdate == null) return false;
        return lastpaymentdate.isAfter(LocalDate.now().minusYears(1));
    }
    
    
    @Override
    public String toString() {
        return "Member{" +
               "id=" + getId() +
               ", name='" + getName() + '\'' +
               ", firstname='" + getFirstname() + '\'' +
               ", balance=" + balance +
               ", categories=" + categories +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Member member = (Member) obj;
        return Double.compare(member.balance, balance) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Double.hashCode(balance);
        return result;
    }
}