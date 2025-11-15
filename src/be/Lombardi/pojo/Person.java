package be.Lombardi.pojo;

public abstract class Person {
	private int id;
	private String name;
	private String firstname;
	private String tel;
	private String username;
	private String password;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFirstname() {
		return firstname;
	}
	
	public String getTel() {
		return tel;
	}
	
	public String getUsername() {
		return username;
	}

	
	public String getPassword() {
		return password;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public Person() {}

	
	public Person(int id, String name, String firstname, String tel, String username, String password) {
		this.id = id;
		this.name = name;
		this.firstname = firstname;
		this.tel = tel;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public String toString() {
		return id + " " + name + " " + firstname + " " + tel + " " + username + " " + password;
	}
}

	
	
