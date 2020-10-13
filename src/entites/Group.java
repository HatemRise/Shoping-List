package entites;

import java.util.ArrayList;
import java.util.List;

public class Group extends Entity{
    public final static int serialVersionUID = 4;
    private String name;
    List <Item> items = new ArrayList<Item>();
    Priority priority;

    public Group(String name) {
        this.name = name;
    }

    public Group(String name, List<Item> items) {
        this.name = name;
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Item getItem(String name){
        for(Item item: items){
            if(item.getName().equalsIgnoreCase(name)) return item;
        }
        return null;
    }

    public Item getItem(int index){
        return items.get(index);
    }

    public void addItem(Item item){
        items.add(item);
    }

    public void addItems(List<Item> items){
        this.items.addAll(items);
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
