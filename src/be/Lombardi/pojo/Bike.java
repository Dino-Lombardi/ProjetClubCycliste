package be.Lombardi.pojo;

public class Bike {
	private int id;
	private double weight;
	private String type;
	private double length;
	private Member member;
	private Vehicle vehicle;
	
	public int getId() {
		return id;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public String getType() {
		return type;
	}
	
	public double getLength() {
		return length;
	}
	
	public Member getMember() {
		return member;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setLength(double length) {
		this.length = length;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	public Bike(int id, double weight, String type, double length, Member member, Vehicle vehicle) {
		this.id = id;
		this.weight = weight;
		this.type = type;
		this.length = length;
		this.member = member;
		this.vehicle = vehicle;
	}

	

	
	
	
}
