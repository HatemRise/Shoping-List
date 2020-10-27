package entites;

import entites.Entity;
import entites.Group;
import entites.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopingList extends Entity implements Serializable {
    public final static int serialVersionUID = 4;
    private String name;
    private List<Item> items = new ArrayList<Item>();
    private List<Group> groups = new ArrayList<Group>();

    public ShopingList(String name, List items) {
        this.name = name;
        this.items = items;
        getItemsGroups();
    }

    public ShopingList(String name) {
        this.name = name;
    }

    public void addItem(Item item){
        this.items.add(item);
        addItemsGroup(item);
    }

    public void addItem(int index, Item item){
        this.items.add(index, item);
        addItemsGroup(item);
    }

    public void addAll(List<Item> items){
        this.items.addAll(items);
        getItemsGroups();
    }

    public void addAll(int index, List<Item> items){
        this.items.addAll(index, items);
        getItemsGroups();
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

    public void setList(List<Item> items){
        this.items = items;
    }

    public Item getItem(int index){
        return items.get(index);
    }

    private void getItemsGroups(){
        this.items.parallelStream().filter(item -> item.getGroup() != null)
                .forEachOrdered(item -> this.groups.add(item.getGroup()));
    }

    private void addItemsGroup(Item item){
        if(item.getGroup() != null && !groups.contains(item.getGroup())) groups.add(item.getGroup());
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
