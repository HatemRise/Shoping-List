package entites;

import java.io.Serializable;

public abstract class Entity implements Serializable {
    public abstract String getName();
    public abstract void setName(String name);
    public abstract boolean isItem();
}
