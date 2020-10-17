package GUI;

import com.jfoenix.controls.JFXListView;
import entites.Group;
import entites.Item;
import javafx.beans.binding.Bindings;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class HBoxGroup extends HBox {
    Group group;
    Label icon = new Label();
    Label name = new Label();
    List<HBoxItem> list = new ArrayList<HBoxItem>();
    boolean expanded = false;


    public HBoxGroup(Group group){
        this.group = group;
        this.setMaxWidth(475);
        name.setText(this.group.getName());
        this.getChildren().addAll(icon, name);
        updateGroupItems();
        setClasses();
    }

    public void updateGroupItems(){
        if(this.group.getItems() != null){
            for(Item item : this.group.getItems()){
                list.add(new HBoxItem(item));
            }
        }
    }

    private void setClasses(){
        this.getStyleClass().add("groupItem");
        name.getStyleClass().add("groupName");
        icon.getStyleClass().add("groupIcon");
    }

    public boolean isExpanded(){return expanded;}

    public void setExpandedState(boolean expanded){
        this.expanded = expanded;
    }

    public List<HBoxItem> getList(){
        return this.list;
    }
}
