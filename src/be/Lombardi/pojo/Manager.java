package be.Lombardi.pojo;

public class Manager extends Person{
	private CategoryType category;

	public CategoryType getCategory() {
		return category;
	}

	public void setCategory(CategoryType category) {
		this.category = category;
	}

	public Manager() {
		super();
	}

	public Manager(int id, String name, String firstname, String tel, String username, String password, CategoryType category) {
		super(id, name, firstname, tel, username, password);
		this.category = category;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + " " + category;
	}
	
	

}
