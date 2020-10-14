package entites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public final static int serialVersionUID = 3;
    private String name;
    private String password;
    private List<Link> lists = new ArrayList<Link>();

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public List getLists() {
        return lists;
    }

    public void setLists(List<Link> lists) {
        this.lists = lists;
    }

    public Link getList(int index){
        try {
            return this.lists.get(index);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public void removeList(int index){
        this.lists.remove(index);
    }

    public void addLink(Link link){
        this.lists.add(link);
    }

    public void addAllToList(List<Link> link){
        this.lists.addAll(link);
    }

    public User(String name, String password, List<Link> lists) {
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

    public String getName() {
        return null;
    }

    public void setName(String name) {

    }
}
