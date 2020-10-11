package entites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Reg implements Serializable {
    private List<User> users = new ArrayList<User>();
    public void Reg(){

    }

    public List logIn(String name, String password){
        for(User user: users){
            if(user.getName().equalsIgnoreCase(name)){
                if(user.getPassword().equals(password)){
                    return user.getLists();
                }
            }
        }
        return null;
    }

    public boolean logOn(String name, String password){
        for(User user: users){
            if(user.getName().equalsIgnoreCase(name)){
                return false;
            }
        }
        users.add(new User(name, password));
        return true;
    }

    private void authorization(User user){

    }
}
