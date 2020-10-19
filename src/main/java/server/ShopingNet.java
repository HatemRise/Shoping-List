package server;

import entites.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import server.Connection;
import server.ShopingList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ShopingNet implements Connection {
    private final String HOST = "http://77.222.54.80:5606";
    CloseableHttpClient client = HttpClients.createDefault();
    ObjectOutputStream objectOutputStream;
    CloseableHttpResponse res = null;
    HttpEntity entity;
    String responseString = "";

    @Override
    public List<Link> logIn(User user) {
        HttpPost httpRequest = new HttpPost(HOST + "/api/login");
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());

        try {
            res = client.execute(httpRequest);
            entity = res.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            System.out.println("Не удается выполнить запрос");
            e.printStackTrace();
        }
        System.out.println(responseString);
        String[] array = responseString.split(",", -1);

        List<Link> lst = new ArrayList<>();
        for (String el : array) {
            Link link = new Link();
            link.setRemote(el);
            lst.add(link);
        }
        try {
            EntityUtils.consume(entity);
            res.close();
        } catch (IOException e) {
            System.out.println("Не удается закрыть соединение");
            e.printStackTrace();
        }
        return lst;
    }

    @Override
    public boolean change(User user, Link link, ShopingList shopingList) {
        HttpPost httpRequest = new HttpPost(HOST + "/api/save");
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());
        httpRequest.setHeader("listname", shopingList.getName());
        try {
            objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream("temp_serial_item"));

            Objects.requireNonNull(objectOutputStream).writeObject(shopingList);
            objectOutputStream.close();
        } catch (IOException e) {
            System.out.println("Не удается записать файл");
            e.printStackTrace();
        }

        FileEntity bin = new FileEntity(new File("temp_serial_item"));
        httpRequest.setEntity(bin);
        try {
            res = client.execute(httpRequest);
            entity = res.getEntity();
            EntityUtils.consume(entity);
            res.close();
        } catch (IOException e) {
            System.out.println("Не удается закрыть соединение");
            e.printStackTrace();
        }
        return res.getStatusLine().getStatusCode() == 200;
    }


    @Override
    public boolean delete(User user, Link link) {
        HttpPost httpRequest = new HttpPost(HOST + "/api/remove");
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());
        httpRequest.setHeader("listname", link.getRemote());
        CloseableHttpResponse res = null;
        try {
            res = client.execute(httpRequest);
        } catch (IOException e) {
            System.out.println("Не удается выполнить запрос");
            e.printStackTrace();
        }

        System.out.println(Objects.requireNonNull(res).getStatusLine());
        entity = res.getEntity();
        try {
            EntityUtils.consume(entity);
            res.close();
        } catch (IOException e) {
            System.out.println("Не удается закрыть соединение");
            e.printStackTrace();
        }
        return res.getStatusLine().getStatusCode() == 200;
    }

    @Override
    public String create(User user, ShopingList shopingList) {
        HttpPost httpRequest = new HttpPost(HOST + "/api/save");
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());
        httpRequest.setHeader("listname", shopingList.getName());

        try {
            objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream("temp_serial_item"));
            Objects.requireNonNull(objectOutputStream).writeObject(shopingList);
            objectOutputStream.close();
        } catch (IOException e) {
            System.out.println("Не удается создать файл");
            e.printStackTrace();
        }

        FileEntity bin = new FileEntity(new File("temp_serial_item"));
        httpRequest.setEntity(bin);
        String responseString = "";
        try {
            res = client.execute(httpRequest);
            entity = res.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);
            res.close();
        } catch (IOException e) {
            System.out.println("Не удается закрыть соединение");
            e.printStackTrace();
        }
        return HOST + "/api/getlist/" + responseString;
    }

    @Override
    public File getList(User user, Link link) {
        String lnk = HOST + "/api/glist/" + link.getRemote();
        HttpGet httpRequest = new HttpGet(lnk);
        httpRequest.setHeader("Content-Type", "text/binary");
        try {
            res = client.execute(httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = Objects.requireNonNull(res).getEntity();
        String filePath = "temp_serial_item";
        try {
            BufferedInputStream bis = new BufferedInputStream(entity.getContent());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            int inByte;
            while ((inByte = bis.read()) != -1) bos.write(inByte);
            bis.close();
            bos.close();

            res.close();
        } catch (IOException e) {
            System.out.println("Не удается закрыть соединение/Считать файл");
            e.printStackTrace();
        }
        return new File(filePath);
    }

    @Override
    public boolean addToMyLists(User user, Link link) {
        HttpPost httpRequest = new HttpPost(HOST + "/api/share/" + link.getRemote());
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());

        try {
            res = client.execute(httpRequest);
        } catch (IOException e) {
            System.out.println("Не удается выполнить запрос");
            e.printStackTrace();
        }

        HttpEntity entity = Objects.requireNonNull(res).getEntity();
        String responseString = null;
        try {
            responseString = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);
            res.close();
        } catch (IOException e) {
            System.out.println("Не удается закрыть соединение");
            e.printStackTrace();
        }

        return Objects.requireNonNull(responseString).contains("EEXIST") || responseString.equals("true");

    }
}
