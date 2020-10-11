package entites;

import java.io.Serializable;

public enum Priority implements Serializable {
    High {
        @Override
        public String toString() {
            return "High";
        }
    },
    Mid{
        @Override
        public String toString() {
            return "Mid";
        }
    },
    Low{
        @Override
        public String toString() {
            return "Low";
        }
    };
    public static Priority getDefault(){
        return Low;
    }
    public abstract String toString();
}
