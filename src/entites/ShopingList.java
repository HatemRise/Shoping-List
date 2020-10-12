package entites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShopingList extends Entity implements Serializable {
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

    public void addAll(List<Entity> items){
        this.items.addAll(items);
    }

    public void remove(int index){
        this.items.remove(index);
    }

    public void removeAll(){
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
