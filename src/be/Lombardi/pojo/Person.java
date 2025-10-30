package be.Lombardi.pojo;

public abstract class Person {
	private int id;
	private String name;
	private String firstname;
	private String tel;
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
	
	public void setPassword(String password) {
		this.password = password;
	}

	public Person() {}

	
	public Person(int id, String name, String firstname, String tel, String password) {
		this.id = id;
		this.name = name;
		this.firstname = firstname;
		this.tel = tel;
		this.password = password;
	}
	
		
}
