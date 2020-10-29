package entites;

import java.util.ArrayList;
import java.util.List;

public class Group extends Entity{
    public final static int serialVersionUID = 4;
    private String name;
    int length;

    public Group(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
