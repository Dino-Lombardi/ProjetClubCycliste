package be.Lombardi.pojo;

public class Inscription {
	private int id;
	private boolean isPassenger;
	private boolean hasBike;
	private Member member;
	private Ride ride;
	
	public int getId() {
		return id;
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
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setIsPassenger(boolean isPassenger) {
		this.isPassenger = isPassenger;
	}
	
	public void setHasBike(boolean hasBike) {
		this.hasBike = hasBike;
	}
	
	public void setMember(Member member) {
		this.member = member;
	}
	
	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public Inscription() {
		// Constructeur par défaut
	}

	public Inscription(int id, boolean ispassenger, boolean hasBike, Member member, Ride ride) {
		this.id = id;
		this.isPassenger = ispassenger;
		this.hasBike = hasBike;
		this.member = member;
		this.ride = ride;
	}
	
	
	
	
	
}
