package sample;

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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ShopingNet implements Connection {
    private final String HOST = "http://localhost:5605";
    CloseableHttpClient client = HttpClients.createDefault();
    ObjectOutputStream objectOutputStream;

    @Override
    public List<Link> logIn(User user) throws IOException {
        HttpPost httpRequest = new HttpPost(HOST + "/api/login");
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());

        CloseableHttpResponse res = client.execute(httpRequest);
        HttpEntity entity = res.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        System.out.println(responseString);
        String[] array = responseString.split(",", -1);
        System.out.println(array.length);
        List<Link> lst = new ArrayList<>();
        for (String el : array) {
            Link link = new Link();
            link.setRemote(el);
            lst.add(link);
        }
        EntityUtils.consume(entity);
        res.close();
        return lst;
    }

    @Override
    public boolean change(User user, Link link, ShopingList shopingList) throws IOException {
        HttpPost httpRequest = new HttpPost(HOST + "/api/save");
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());
        httpRequest.setHeader("listname", shopingList.getName());

        objectOutputStream = new ObjectOutputStream(
                new FileOutputStream("temp_serial_item"));

        Objects.requireNonNull(objectOutputStream).writeObject(shopingList);
        objectOutputStream.close();

//        StringEntity raw = new StringEntity(getClass().getResource("itm"));
        FileEntity bin = new FileEntity(new File("temp_serial_item"));
        httpRequest.setEntity(bin);
        CloseableHttpResponse res = client.execute(httpRequest);


        System.out.println(res.getStatusLine().getStatusCode());
        HttpEntity entity = res.getEntity();
        EntityUtils.consume(entity);

        res.close();
        return res.getStatusLine().getStatusCode() == 200;
    }


    @Override
    public boolean delete(User user, Link link) throws IOException {
        HttpPost httpRequest = new HttpPost(HOST + "/api/remove");
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());
        httpRequest.setHeader("listname", link.getRemote());
        CloseableHttpResponse res = client.execute(httpRequest);

        System.out.println(res.getStatusLine());
        HttpEntity entity = res.getEntity();
        EntityUtils.consume(entity);

        res.close();
        return res.getStatusLine().getStatusCode() == 200;
    }

    @Override
    public String create(User user, ShopingList shopingList) throws IOException {
        HttpPost httpRequest = new HttpPost(HOST + "/api/save");
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());
        httpRequest.setHeader("listname", shopingList.getName());

        objectOutputStream = new ObjectOutputStream(
                new FileOutputStream("temp_serial_item"));

        Objects.requireNonNull(objectOutputStream).writeObject(shopingList);
        objectOutputStream.close();

//        StringEntity raw = new StringEntity(getClass().getResource("itm"));
        FileEntity bin = new FileEntity(new File("temp_serial_item"));
        httpRequest.setEntity(bin);
        CloseableHttpResponse res = client.execute(httpRequest);

        HttpEntity entity = res.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");

        EntityUtils.consume(entity);
        res.close();
        return HOST + "/api/getlist/" + responseString;
    }

    @Override
    public File getList(User user, Link link) throws IOException {
        String lnk = HOST + "/api/glist/" + link.getRemote();
        HttpGet httpRequest = new HttpGet(lnk);
        httpRequest.setHeader("Content-Type", "text/binary");
        CloseableHttpResponse res = client.execute(httpRequest);
        HttpEntity entity = res.getEntity();

        BufferedInputStream bis = new BufferedInputStream(entity.getContent());
        String filePath = "temp_serial_item";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
        int inByte;
        while ((inByte = bis.read()) != -1) bos.write(inByte);
        bis.close();
        bos.close();

        res.close();
        return new File(filePath);
    }

    @Override
    public boolean addToMyLists(User user, Link link) throws IOException {
        HttpPost httpRequest = new HttpPost(HOST + "/api/share/"+link.getRemote());
        httpRequest.setHeader("Content-Type", "text/binary");
        httpRequest.setHeader("login", user.getName());
        httpRequest.setHeader("password", user.getPassword());

        CloseableHttpResponse res = client.execute(httpRequest);

        HttpEntity entity = res.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        EntityUtils.consume(entity);
        res.close();
        return responseString.contains("EEXIST") || responseString.equals("true");

    }
}
