package GUI;

import com.jfoenix.controls.JFXRippler;
import entites.Entity;
import entites.Group;
import entites.Item;
import entites.Priority;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Arrays;

public class ShopingListCell extends HBox {
    private HBox root = new HBox();
    private JFXRippler rippler = new JFXRippler(root);
    private Label name;
    private Label quantity;
    private Text description;
    private String groupName = null;
    private int itemHashCode;

    public ShopingListCell(Item item) {
        if(item.getGroup() != null){
            groupName = item.getGroup().getName();
        }
        this.itemHashCode = item.hashCode();
        root.setPrefWidth(470);
        Item temp = (Item) item;
        this.name = new Label(temp.getName());
        this.quantity = new Label(String.valueOf(temp.getQuantity()));
        this.description = new Text(temp.getDescription());
        name.getStyleClass().addAll("itemName", "property");
        quantity.getStyleClass().addAll("itemQuantity", "property");
        description.getStyleClass().addAll("itemDescription", "property");
        this.getStyleClass().addAll("cell", "itemCell", temp.getPriority().name());
        root.getChildren().addAll(this.name, this.quantity, this.description);
        this.getChildren().add(rippler);
    }

    public void update(Item item){
        Item temp = (Item) item;
        this.name.setText(temp.getName());
        this.quantity.setText(String.valueOf(temp.getQuantity()));
        this.description.setText(temp.getDescription());
        this.getStyleClass().removeAll(Arrays.asList(Priority.values()));
        this.getStyleClass().add(temp.getPriority().name());

    }

    public Label getName() {
        return name;
    }

    public void setName(Label name) {
        this.name = name;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int itemHashCode(){
        return this.itemHashCode;
    }
}
