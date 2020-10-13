package entites;

import java.io.Serializable;

public enum Priority implements Serializable {
    High {
        private int value = 1;

        @Override
        protected int getValue() {
            return value;
        }

        @Override
        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() { return "High"; }
    },
    Mid{
        private int value = 2;

        @Override
        protected int getValue() {
            return value;
        }

        @Override
        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Mid";
        }
    },
    Low{
        private int value = 5;

        @Override
        protected int getValue() {
            return value;
        }

        @Override
        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Low";
        }
    },
    Unnecessary{
        private int value = 10;

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Unnecessary";
        }
    };
    public Priority increasePriority(){
        return values()[this.ordinal() - 1  > 0 ? this.ordinal() - 1 : 0].getValue() != -1
                ? values()[this.ordinal() - 1  > 0 ? this.ordinal() - 1 : 0]
                : (values()[this.ordinal() - 1  > 0 ? this.ordinal() - 1 : 0]
                != values()[0]
                ? values()[this.ordinal() - 1  > 0 ? this.ordinal() - 1 : 0].increasePriority()
                : values()[this.ordinal() - 1  > 0 ? this.ordinal() - 1 : 0].lowerPriority());
    }
    public Priority lowerPriority(){
        return values()[this.ordinal() + 1 < values().length ? this.ordinal() + 1 : values().length - 1].getValue() != -1
                ? values()[this.ordinal() + 1 < values().length ? this.ordinal() + 1 : values().length - 1]
                : (values()[this.ordinal() + 1 < values().length ? this.ordinal() + 1 : values().length - 1]
                != values()[values().length - 1]
                ? values()[this.ordinal() + 1 < values().length ? this.ordinal() + 1 : values().length - 1].lowerPriority()
                : values()[this.ordinal() + 1 < values().length ? this.ordinal() + 1 : values().length - 1].increasePriority());
    }
    public static Priority getDefault(){
        return Low;
    }
    protected abstract int getValue();
    public abstract void setValue(int value);
    public Priority getNewPriority(int value){
        if(value < this.increasePriority().getValue() && this.increasePriority() != this){
            return this.increasePriority().getNewPriority(value);
        }
        if(value > this.lowerPriority().getValue() && this != this.lowerPriority()){
            return this.lowerPriority().getNewPriority(value);
        }
        return this;
    }
    public abstract String toString();
}
