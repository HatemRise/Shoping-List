package entites;
import java.io.*;

public class Settings implements Serializable {
    User user;
    String server_id;
    int update_interval;
    boolean autoupdate;

    public Settings (){
        User user = null;
        String server_id = null;
        int update_interval = 0;
        boolean autoupdate = false;
    }
    public Settings (User user, String server_id, int update_interval, boolean autoupdate){
        this.user = user;
        this.server_id = server_id;
        this.update_interval = update_interval;
        this.autoupdate = autoupdate;
    }
    public User getUser(){
        return user;
    }
    public void setUser(User value){
        user = value;
    }
    public String getServer_id(){
        return server_id;
    }
    public void setServer_id(String value){
        server_id = value;
    }
    public int getUpdate_interval(){
        return update_interval;
    }
    public void setUpdate_interval(int value){
        update_interval = value;
    }
    public boolean getAutoupdate(){
        return autoupdate;
    }
    public void setAutoupdate(boolean value) {
        autoupdate = value;
    }
    static void serialize(Settings empObj) throws IOException {
        try (FileOutputStream fos = new FileOutputStream("data.obj");
             ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(empObj);
        }
    }
}
