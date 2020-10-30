package entites;

import java.io.Serializable;

public class Link implements Serializable {
    public final static int serialVersionUID = 12;
    String remote;
    String local;
    String name;

    public Link() {
    }

    /**Принимает ссылку на удалёный репозиторий в качестве аргумента*/
    public Link(String remote) {
        this.remote = remote;
    }

    public Link(String remote, String local) {
        this.remote = remote;
        this.local = local;
    }

    public String getRemote() {
        return remote;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String toString(){
        return this.name == null ? this.remote : this.name;
    }
}
