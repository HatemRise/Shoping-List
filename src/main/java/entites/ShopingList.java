package entites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShopingList extends Entity implements Serializable {
    public final static int serialVersionUID = 4;
    private String name;
    private List<Entity> items = new ArrayList<Entity>();

    public ShopingList(String name, List items) {
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

    public void addAll(List<Entity> items){
        this.items.addAll(items);
    }

    public void addAll(int index, List<Entity> items){
        this.items.addAll(index, items);
    }

    public void remove(int index){
        this.items.remove(index);
    }

    public void remove(Entity item){
        this.items.remove(item);
    }

    public void removeAll(List<Entity> items){this.items.removeAll(items);}

    public void clear(){
        this.items.clear();
    }

    public Entity getElement(int index){
        return this.items.get(index);
    }

    public List getList(){
        return this.items;
    }

    public void setList(List<Entity> items){
        this.items = items;
    }

    public Group getGroup(int index){
        return items.get(index).isItem() ? null : (Group) items.get(index);
    }

    public Item getItem(int index){
        return items.get(index).isItem() ? (Item) items.get(index) : null;
    }

    @Override
    public boolean isItem() {
        return false;
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
