package server;

import entites.Link;
import entites.Reg;
import entites.ShopingList;
import entites.User;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LocalConnect implements Connection{
    private final String serverPath = "C/TestServer";
    @Override
    public List<Link> logIn(User user) {
        try {
            FileInputStream in = new FileInputStream(serverPath + "/reg.shl");
            ObjectInputStream regStream = new ObjectInputStream(in);
            Reg reg = (Reg)regStream.readObject();
            in.close();
            regStream.close();
            if(reg.logIn(user.getName(), user.getPassword()) == null){
                reg.logOn(user.getName(), user.getPassword());
                FileOutputStream out = new FileOutputStream(serverPath + "/reg.shl");
                ObjectOutputStream outRegStream = new ObjectOutputStream(out);
                outRegStream.writeObject(reg);
                outRegStream.flush();
                out.flush();
                outRegStream.close();
                out.close();
                return new ArrayList<Link>();
            }else {
                return reg.logIn(user.getName(), user.getPassword());
            }
        } catch (FileNotFoundException e) {
            File regPath = new File(serverPath);
            File reg = new File(regPath+ "/reg.shl");
            if(!regPath.exists())regPath.mkdir();
            if(!reg.exists()) {
                try {
                    if(reg.createNewFile()){
                        return logIn(user);
                    }
                } catch (IOException ioException) {
                    System.out.println("Ошибка создания файла реестра");
                    return null;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка чтения файла реестра");
            return null;
        }
        return null;
    }

    @Override
    public boolean change(User user, Link link, ShopingList shopingList) {
        File list = new File(link.getRemote());
        try {
            FileOutputStream out = new FileOutputStream(list);
            ObjectOutputStream newShopingList = new ObjectOutputStream(out);
            newShopingList.writeObject(shopingList);
            newShopingList.flush();
            out.flush();
            newShopingList.close();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean delete(User user, Link link) {
        File list = new File(link.getRemote());
        if(list.delete()) return true;
        return false;
    }

    @Override
    public String create(User user, ShopingList shopingList) {
        String hash = getHash(user.getName() + " " + user.getPassword()) + "-"+ shopingList.getName() + ".shllf";
        try {
            FileOutputStream out = new FileOutputStream( serverPath + "/" + hash);
            ObjectOutputStream newShopingList = new ObjectOutputStream(out);
            newShopingList.writeObject(shopingList);
            newShopingList.flush();
            out.flush();
            newShopingList.close();
            out.close();
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    @Override
    public File getList(User user, Link link) {
        return new File(link.getRemote());
    }

    @Override
    public boolean addToMyLists(User user, Link link) {
        try {
            FileInputStream in = new FileInputStream(serverPath + "/reg.shl");
            ObjectInputStream regStream = new ObjectInputStream(in);
            Reg reg = (Reg) regStream.readObject();
            in.close();
            regStream.close();
            reg.addToList(user, link);
            FileOutputStream out = new FileOutputStream(serverPath + "/reg.shl");
            ObjectOutputStream outRegStream = new ObjectOutputStream(out);
            outRegStream.writeObject(reg);
            outRegStream.flush();
            out.flush();
            outRegStream.close();
            out.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private String getHash(String loginPass) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(loginPass.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
            return myHash;
        }catch (Exception e){
            return null;
        }
    }
}
