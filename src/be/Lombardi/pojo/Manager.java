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
               ", category=" + getCategory() +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Manager manager = (Manager) obj;
        return getCategory() == manager.getCategory();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
        return result;
    }
}