package entites;

import java.io.Serializable;

public class Item extends Entity implements Serializable {
    public final static int serialVersionUID = 5;
    private String name;
    private int quantity;
    private Priority priority;
    private String description;
    private double priorityFactor = 1;

    public Item(String name){
        this.name = name;
        this.quantity = 0;
        this.priority = Priority.getDefault();
        this.description = "";
    }

    public Item(Item item) {
        this.name = item.name;
        this.quantity = item.quantity;
        this.priority = item.priority;
        this.description = item.description;
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

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public double getPriorityFactor() {
        return priorityFactor;
    }

    public void setPriorityFactor(double priorityFactor) {
        this.priorityFactor = priorityFactor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void bought(){ this.priority = Priority.Unnecessary; }

    @Override
    public boolean isItem() {
        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {

    }
}
