package server;

import entites.Link;
import entites.ShopingList;
import entites.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
//        System.out.println(responseString);
        String[] array = responseString.substring(1, responseString.length() - 1).split(",", -1);
        List<Link> lst = new ArrayList<>();
        if (responseString.equals("[]")) {
            return lst;
        }
        for (String el : array) {
            Link link = new Link();
            link.setRemote(el.substring(1, el.length() - 1));
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
    public boolean delete(User user, Link link) {
        HttpPost httpRequest = new HttpPost(HOST + "/api/remove");
        String parsedLink = link.getRemote().split("http://77.222.54.80:5606/api/glist/\\w+/")[1];
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());
        httpRequest.setHeader("listname", parsedLink);
        CloseableHttpResponse res = null;
        try {
            res = client.execute(httpRequest);
        } catch (IOException e) {
            System.out.println("Не удается выполнить запрос");
            e.printStackTrace();
        }

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
    public String save(User user, ShopingList shopingList) {
        try {
            objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream("temp_serial_item"));
            Objects.requireNonNull(objectOutputStream).writeObject(shopingList);
            objectOutputStream.close();
        } catch (IOException e) {
            System.out.println("Не удается создать файл");
            e.printStackTrace();
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();
        MultipartEntityBuilder entitybuilder = MultipartEntityBuilder.create();
        entitybuilder.addBinaryBody(shopingList.getName(), new File("temp_serial_item"));
        HttpEntity mutiPartHttpEntity = entitybuilder.build();
        RequestBuilder reqbuilder= RequestBuilder.post(HOST + "/api/save");
        reqbuilder.addHeader("login", user.getName());
        reqbuilder.addHeader("password", user.getPassword());
        reqbuilder.addHeader("listname", shopingList.getName());
        reqbuilder.setEntity(mutiPartHttpEntity);
        HttpUriRequest multipartRequest = reqbuilder.build();

        HttpResponse httpresponse = null;
        try {
            httpresponse = httpclient.execute(multipartRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        return HOST + "/api/glist/" + responseString;
    }

    @Override
    public File getList(User user, Link link) {
        String lnk = link.getRemote();
        HttpGet httpRequest = new HttpGet(lnk);
        httpRequest.setHeader("Content-Type", "application/octet-stream");
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
            bos.flush();
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
        String parsedLink = link.getRemote().split("http://77.222.54.80:5606/api/glist/")[1];
        HttpPost httpRequest = new HttpPost(HOST + "/api/share/" + parsedLink);
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
