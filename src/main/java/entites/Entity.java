package entites;

import java.io.Serializable;

public abstract class Entity implements Serializable {
    public final static int serialVersionUID = 1;
    public abstract String getName();
    public abstract void setName(String name);
    @Override public abstract int hashCode();
}
