package entites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends Entity implements Serializable {
    private String name;
    private String password;
    private List<ShopingList> lists = new ArrayList<ShopingList>();

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public List getLists() {
        return lists;
    }

    public void setLists(List<ShopingList> lists) {
        this.lists = lists;
    }

    public ShopingList getList(int index){
        try {
            return (ShopingList) lists.get(index);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public ShopingList getList(String name){
        for(ShopingList list : lists){
            if(list.getName().equalsIgnoreCase(name)){
                return list;
            }
        }
        return null;
    }

    public void removeList(int index){
        lists.remove(index);
    }

    public void removeList(ShopingList list){
        lists.remove(list);
    }

    public void removeList(String name){
        ShopingList list = getList(name);
        if(list != null) removeList(list);
    }

    public void addToList(ShopingList list){
        this.lists.add(list);
    }

    public void addAllToList(List<ShopingList> lists){
        this.lists.addAll(lists);
    }

    public void createList(String name){
        lists.add(new ShopingList(name));
    }

    public void createList(ShopingList list){
        lists.add(list);
    }

    public User(String name, String password, List lists) {
        this.name = name;
        this.password = password;
        this.lists = lists;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        //Его тут надо обязательно хэшировать
        //и хранить только в виде хэша
        //но пока и так сойдёт
        this.password = password;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }
}
