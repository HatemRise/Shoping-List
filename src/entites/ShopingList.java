package entites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShopingList extends Entity implements Serializable {
    private String name;
    private List lists = new ArrayList<String>();

    public ShopingList(String name, List lists) {
        this.name = name;
        this.lists = lists;
    }

    public ShopingList(String name) {
        this.name = name;
    }

    public ShopingList() {
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
