package entites;

import java.io.Serializable;

public class Item extends Entity implements Serializable {
    public final static int serialVersionUID = 5;
    private String name;
    private int quantity;
    private Priority priority;
    private String description;
    private Group group;

    public Item(String name){
        this.name = name;
        this.quantity = 0;
        this.priority = Priority.getDefault();
        this.description = "";
        this.group = null;
    }

    public Item(Item item) {
        this.name = item.name;
        this.quantity = item.quantity;
        this.priority = item.priority;
        this.description = item.description;
        this.group = item.getGroup();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = Integer.parseInt(quantity);
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void bought(){ this.priority = Priority.Unnecessary; }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return "Item " + this.name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode() ? true : false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() + this.quantity + this.description.hashCode();
    }
}
