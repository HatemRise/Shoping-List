package GUI;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXScrollPane;
import entites.Entity;
import entites.Group;
import entites.Item;
import entites.ShopingList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShopingListView extends VBox {
    private SimpleIntegerProperty selected = new SimpleIntegerProperty(-1);
    private ObservableList<ShopingListCell> cells = FXCollections.observableArrayList();
    private ObservableList<Item> items = FXCollections.observableArrayList();
    private ShopingListView thisClass = this;

    public ShopingListView() {
        init();
    }

    public ShopingListView(List<Item> list){
        this.items.addAll(list);
    }

    private void init(){
        items.addListener(new ListChangeListener<Item>() {
            @Override
            public void onChanged(Change<? extends Item> change) {
                change.next();
                List<ShopingListCell> temp = new ArrayList<>();
                change.getAddedSubList().stream().forEach(i -> temp.add(new ShopingListCell(i)));
                cells.addAll(change.getFrom(), temp);
                cells.removeAll(removeFromGUI((List<Item>) change.getRemoved()));
                temp.stream().filter(i -> i.getGroupName() != null)
                        .forEachOrdered(i -> {
                            if(!isThisGroupExist(i.getGroupName())){
                                cells.add(cells.indexOf(i), new ShopingListGroupCell(i.getGroupName()));
                            }
                            i.managedProperty().bind(i.visibleProperty());
                            i.setVisible(false);
                        });
            }
        });
        cells.addListener(new ListChangeListener<ShopingListCell>() {
            @Override
            public void onChanged(Change<? extends ShopingListCell> change) {
                change.next();
                thisClass.getChildren().addAll(change.getFrom(), change.getAddedSubList());
                thisClass.getChildren().removeAll(change.getRemoved());
                change.getAddedSubList().parallelStream().forEach(i ->{
                    i.setOnMouseClicked(action ->{
                        if(i.getClass().equals(ShopingListGroupCell.class)){
                            ShopingListGroupCell temp = (ShopingListGroupCell) i;
                            if(temp.isExpanded()){
                                cells.parallelStream().filter(item -> item.getGroupName() != null && item.getGroupName().equals(temp.getName().getText())).forEach(item -> {
                                    item.managedProperty().bind(item.visibleProperty());
                                    item.setVisible(false);
                                });
                                temp.setExpanded(false);
                                if(!getSelectedCell().isVisible()){
                                    selected.set(0);
                                    setSelectionCell();
                                }
                            } else {
                                cells.parallelStream().filter(item -> item.getGroupName() != null && item.getGroupName().equals(temp.getName().getText())).forEach(item -> item.setVisible(true));
                                temp.setExpanded(true);
                            }
                        } else {
                            selected.set(cells.indexOf(i));
                            setSelectionCell();
                        }
                    });
                });
            }
        });
    }

    private void setSelectionCell(){
        cells.parallelStream().forEach(item -> item.getStyleClass().remove("selected"));
        cells.get(selected.get()).getStyleClass().add("selected");
    }

    public void update(){
        items.stream().forEach(i -> cells.add(new ShopingListCell(i)));
        this.getChildren().addAll(cells);
    }

    public ObservableList<ShopingListCell> getCells(){
        return cells;
    }

    public void setCells(ObservableList<ShopingListCell> cells) {
        this.cells = cells;
    }

    public ObservableList<Item> getItems() {
        return items;
    }

    public void setItems(ObservableList<Item> items) {
        this.items = items;
    }

    public void updateCell(Item item, int index){
        cells.get(index).update(item);
    }

    public void updateItem(){

    }

    public int getSelected() {
        return selected.get();
    }

    public Item getSelectedItem() {
        return items.size() > 0 ? items.get(getIndexForItemsArray()) : null;
    }

    public ShopingListCell getSelectedCell() {
        return cells.get(selected.get());
    }

    public void setSelected(int selected) {
        this.selected.set(selected);
        setSelectionCell();
    }

    public SimpleIntegerProperty selectedProperty() {
        return selected;
    }

    private boolean isThisGroupExist(String name){
        if(name == null){
            return false;
        }
        return cells.parallelStream()
                .anyMatch(i -> i.getClass().equals(ShopingListGroupCell.class)
                        && i.getName().getText().equals(name));
    }

    private int getIndexForItemsArray(){
        int hash = getSelectedCell().itemHashCode();
        return items.indexOf(items.parallelStream().filter(item -> item.hashCode() == hash).findFirst().get());
    }

    private List<ShopingListCell> removeFromGUI(List<Item> items){
       return cells.parallelStream().filter(cell ->
                items.parallelStream().anyMatch(item -> item.hashCode() == cell.itemHashCode()))
                .collect(Collectors.toList());
    }
}
