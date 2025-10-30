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
	
	public boolean getisPassenger() {
		return isPassenger;
	}
	
	public boolean isHasBike() {
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
	
	public void setisPassenger(boolean isPassenger) {
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

	public Inscription(int id, boolean ispassenger, boolean hasBike, Member member, Ride ride) {
		this.id = id;
		this.isPassenger = ispassenger;
		this.hasBike = hasBike;
		this.member = member;
		this.ride = ride;
	}
	
	
	
	
	
}
