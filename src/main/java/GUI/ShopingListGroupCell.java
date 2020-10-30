package GUI;

import entites.Item;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ShopingListGroupCell extends ShopingListCell {
    private boolean expanded = false;

    public ShopingListGroupCell(Item item) {
        super(item);
    }

    public ShopingListGroupCell(String name){
        super(new Item(name));
        super.setDescription(new Text("Group"));
        this.getStyleClass().addAll("groupCell");
        this.getStyleClass().removeAll("itemCell", "Low");
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int size(){
        return Integer.parseInt(super.getQuantity().getText());
    }

    public void setQuantity(int num){
        super.getQuantity().setText(String.valueOf(num));
    }

    @Override
    public Text getDescription() {
        return super.getDescription();
    }

    @Override
    public void setDescription(Text description) {
    }
}
