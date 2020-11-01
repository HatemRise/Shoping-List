package server;

import entites.Link;
import entites.ShopingList;
import entites.User;

import java.io.File;
import java.util.List;

public interface Connection {
    /**Метод для авторизации*/
    public List<Link> logIn(User user);
    /**Метод для удаления файла из удалённого хранилища по ссылке*/
    public boolean delete(User user, Link link) ;
    /**Метод для создания файла в удалённом хрнилище*/
    public String save(User user, ShopingList shopingList) ;
    /**Метод для получить файл из хранилища по ссылке*/
    public File getList(User user, Link link) ;
    /**Метод для добавления существующего файла пользователю*/
    public boolean addToMyLists(User user, Link link) ;
}
