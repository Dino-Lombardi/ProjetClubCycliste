package be.Lombardi.pojo;

public class Manager extends Person {
    private CategoryType category;

    public Manager() {
        super();
    }

    public Manager(int id, String name, String firstname, String tel, String username, CategoryType category) {
        super(id, name, firstname, tel, username);
        this.category = category;
    }

    @Override
    public void validate() {
        super.validate();
        
        if (category == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire pour un manager");
        }
    }

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Manager{" +
               "id=" + getId() +
               ", name='" + getName() + '\'' +
               ", firstname='" + getFirstname() + '\'' +
               ", category=" + category +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Manager manager = (Manager) obj;
        return category == manager.category;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}