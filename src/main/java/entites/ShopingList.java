package entites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShopingList extends Entity implements Serializable {
    public final static int serialVersionUID = 4;
    private String name;
    private ArrayList<Item> items = new ArrayList<Item>();

    public ShopingList(String name, ArrayList items) {
        this.name = name;
        this.items = items;
    }

    public ShopingList(String name) {
        this.name = name;
    }

    public void addItem(Item item){
        this.items.add(item);
    }

    public void addItem(int index, Item item){
        this.items.add(index, item);
    }

    public void addAll(List<Item> items){
        this.items.addAll(items);
    }

    public void addAll(int index, List<Item> items){
        this.items.addAll(index, items);
    }

    public void remove(int index){
        this.items.remove(index);
    }

    public void remove(Entity item){
        this.items.remove(item);
    }

    public void removeAll(List<Item> items){this.items.removeAll(items);}

    public void clear(){
        this.items.clear();
    }

    public Item getElement(int index){
        return this.items.get(index);
    }

    public List getList(){
        return this.items;
    }

    public void setList(ArrayList<Item> items){
        this.items = items;
    }

    public Item getItem(int index){
        return items.get(index);
    }

    public List<Group> getGroups() {
        List<Group> groups = new ArrayList<Group>();
        this.items.parallelStream().filter(item -> item.getGroup() != null).forEach(item -> {
            if(!groups.contains(item.getGroup())) groups.add(item.getGroup());
        });
        return groups;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode() ? true : false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
