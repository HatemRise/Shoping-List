package server;

import entites.Link;
import entites.ShopingList;
import entites.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Connection {
    /**Метод для авторизации*/
    public List<Link> logIn(User user) throws IOException;
    /**Метод сохранения изменений в удалённом хранилище*/
    public boolean change(User user, Link link, ShopingList shopingList) throws IOException;
    /**Метод для удаления файла из удалённого хранилища по ссылке*/
    public boolean delete(User user, Link link) throws IOException;
    /**Метод для создания файла в удалённом хрнилище*/
    public String create(User user, ShopingList shopingList) throws IOException;
    /**Метод для получить файл из хранилища по ссылке*/
    public File getList(User user, Link link) throws IOException;
    /**Метод для добавления существующего файла пользователю*/
    public boolean addToMyLists(User user, Link link) throws IOException;
}
