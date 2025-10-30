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

	public Manager(int id, String name, String firstname, String tel, String password, CategoryType category) {
		super(id, name, firstname, tel, password);
		this.category = category;
	}
	
	
	

}
