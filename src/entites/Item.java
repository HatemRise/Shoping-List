package entites;

import java.io.Serializable;

public class Item extends Entity implements Serializable {
    private String name;
    private int quantity;
    private Priority priority;
    private String description;
    private boolean active;

    public Item(String name){
        this.name = name;
        this.quantity = 0;
        this.priority = Priority.getDefault();
        this.description = "";
        this.active = true;
    }

    public Item(Item item) {
        this.name = item.name;
        this.quantity = item.quantity;
        this.priority = item.priority;
        this.description = item.description;
        this.active = item.active;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
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

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }
}
