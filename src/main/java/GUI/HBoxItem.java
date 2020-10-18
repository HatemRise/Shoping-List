package GUI;

import entites.Item;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class HBoxItem extends HBox {
    Item item;
    Label name;
    Label quantity;
    Text description;

    public HBoxItem(Item item){
        this.item = item;
        this.name = new Label(item.getName());
        this.quantity = new Label(String.valueOf(item.getQuantity()));
        this.description = new Text(item.getDescription());
        this.getStyleClass().add(item.getPriority().name());
        this.getChildren().addAll(name, quantity, description);
        setClasses();
    }

    public void getItemData(){
        this.name = new Label(item.getName());
        this.quantity = new Label(String.valueOf(item.getQuantity()));
        this.description = new Text(item.getDescription());
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Label getName() {
        return name;
    }

    public void setName(Label name) {
        this.name = name;
    }
    public void setName(String name) {
        this.name.setText(name);
    }

    public Label getQuantity() {
        return quantity;
    }

    public void setQuantity(Label quantity) {
        this.quantity = quantity;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public void setDescription(String description) {
        this.description.setText(description);
    }

    private void setClasses(){
        this.getStyleClass().add("item");
        name.getStyleClass().add("itemNameLabel");
        name.getStyleClass().add("property");
        quantity.getStyleClass().add("itemQuantityLabel");
        quantity.getStyleClass().add("property");
        description.getStyleClass().add("itemDescriptionText");
        description.getStyleClass().add("property");
    }

    public void setQuantity(String quantity) {
        this.quantity.setText(quantity);
    }
}
