package server;

import entites.ShopingList;
import entites.User;

import java.util.List;

public interface Connection {
    public List<String> logIn(User user);
    public List<String> update(User user);
    public boolean change(User user, String path, ShopingList shopingList);
    public boolean delete(User user, String path);
    public String create(User user, ShopingList shopingList);
}
