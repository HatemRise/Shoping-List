package sample;

import GUI.HBoxGroup;
import GUI.HBoxItem;
import com.jfoenix.controls.*;
import entites.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import server.Connection;
import server.LocalConnect;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Connection connect;
    private ShopingList list;
    private Settings settings;
    private LogInController logInController;
    private double xOffset, yOffset;


    @FXML
    public Node rootNode;
    private Stage primaryStage;
    @FXML
    private VBox itemPropertiesPane;
    @FXML
    private JFXTextField itemName;
    @FXML
    private JFXButton itemQuantityMinus;
    @FXML
    private JFXTextField itemQuantity;
    @FXML
    private JFXButton itemQuantityPlus;
    @FXML
    private JFXButton itemDescriptionSaveButton;
    @FXML
    private JFXComboBox<Priority> itemPrioritySwitcher;
    @FXML
    private JFXTextArea itemDescription;
    @FXML
    private JFXButton itemRemoveButton;
    @FXML
    JFXListView<HBox> listView;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.settings = getSettings();
        this.connect = new LocalConnect();
        itemPrioritySwitcher.getItems().addAll(Priority.values());
        itemPrioritySwitcher.setPromptText("Priority");
        testDataList();
    }

    private void testDataList(){
        for(int i = 0 ; i < 3; i++) {
            Item item = new Item("name" + i);
            item.setQuantity(12 + i);
            item.setDescription("description" + i);
            listView.getItems().add(getItemWithListener((HBoxItem) itemToListItem(item)));
        }
        for(int j = 0 ; j < 20; j++) {
            Group group = new Group("name" + String.valueOf(j));
            for (int i = 0; i < 20; i++) {
                Item item = new Item("name" + String.valueOf(i));
                item.setQuantity(12 + i);
                item.setDescription("description" + String.valueOf(i));
                group.addItem(item);
            }
            listView.getItems().add(getGroupItemWithListener((HBoxGroup) groupToListItem(group)));
        }
    }

    private HBox getItemWithListener(HBoxItem item){
        item.setOnMouseClicked(mouseEvent -> {
            itemPropertiesPane.setDisable(false);
            itemName.setText(item.getItem().getName());
            itemName.setOnAction(actionEvent -> {
                item.getItem().setName(itemName.getText());
                item.setName(itemName.getText());
                System.out.println(item.getItem().getName());
            });
            itemQuantityMinus.setOnAction(action -> {
                if(Integer.parseInt(itemQuantity.getText()) > 0) {
                    item.getItem().setQuantity(item.getItem().getQuantity() - 1);
                    item.setQuantity(String.valueOf(item.getItem().getQuantity()));
                    itemQuantity.setText(String.valueOf(item.getItem().getQuantity()));
                }
            });
            itemQuantity.setText(String.valueOf(item.getItem().getQuantity()));
            itemQuantity.setOnAction(action -> {
                item.getItem().setQuantity(itemQuantity.getText());
                item.setQuantity(String.valueOf(item.getItem().getQuantity()));
            });
            itemQuantityPlus.setOnAction(action -> {
                item.getItem().setQuantity(item.getItem().getQuantity() + 1);
                item.setQuantity(String.valueOf(item.getItem().getQuantity()));
                itemQuantity.setText(String.valueOf(item.getItem().getQuantity()));
            });
            itemPrioritySwitcher.setOnAction(action -> {
                if(itemPrioritySwitcher.getValue() != item.getItem().getPriority()) {
                    item.getItem().setPriority(itemPrioritySwitcher.getValue());
                    System.out.println(item.getItem().getPriority() + " " + item.getItem().getName());
                }
            });
            itemPrioritySwitcher.setValue(item.getItem().getPriority());
            itemDescription.setText(item.getItem().getDescription());
            itemDescription.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    itemDescriptionSaveButton.setDisable(false);
                }
            });
            itemDescriptionSaveButton.setOnAction(action ->{
                item.getItem().setDescription(itemDescription.getText());
                item.setDescription(itemDescription.getText());
                itemDescriptionSaveButton.setDisable(true);
            });
            itemRemoveButton.setOnAction(action -> {
                list.remove(item.getItem());
                listView.getItems().remove(item);
            });

        });
        return item;
    }

    private HBox getGroupItemWithListener(HBoxGroup listGroup){
        listGroup.setOnMouseClicked(mouseEvent -> {
            if(!listGroup.isExpanded()){
                listView.getItems().addAll(listView.getItems().indexOf(listGroup) + 1, listGroup.getList());
                listGroup.setExpandedState(true);
            }else{
                listView.getItems().removeAll(listGroup.getList());
                listGroup.setExpandedState(false);
            }
        });
        for (HBoxItem item : listGroup.getList()){
            getItemWithListener(item);
        }
        return listGroup;
    }

    public void logIn() throws IOException {
        show(primaryStage, null);
    }

    public void exit(){
        primaryStage.close();
    }

    public void logOut(){
        System.out.println("LogOut Не реализовано!");
    }

    public void save(){
        System.out.println("Save Не реализовано!");
    }

    public void getLists(){
        System.out.println("Get lists не реализовано!");
    }

    public void deleteRemoteFile(){
        System.out.println("Delete remote file не реализовано");
    }

    public void deleteAndDownload(){
        System.out.println("Delete and Download не реализовано");
    }

    public void getChange(){
        System.out.println("Get change не реализовано");
    }

    public void minimizeWindow(){
        primaryStage.setIconified(true);
    }

    public void show(Stage primaryStage, String message) throws IOException {
        this.primaryStage = primaryStage;
        primaryStage.getScene().setOnMousePressed(mouseEvent -> {
            xOffset = primaryStage.getX() - mouseEvent.getScreenX();
            yOffset = primaryStage.getY() - mouseEvent.getScreenY();
        });
        primaryStage.getScene().setOnMouseDragged(mouseEvent -> {
            primaryStage.setX(mouseEvent.getScreenX() + xOffset);
            primaryStage.setY(mouseEvent.getScreenY() + yOffset);
        });
        if(settings.getUser() == null){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/logIn.fxml"));
            Parent root = fxmlLoader.load();
            Stage logIn = new Stage();
            Scene scene = new Scene(root);
            logIn.setScene(scene);
            logIn.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            logIn.setTitle("Log In");
            logIn.initOwner(primaryStage.getScene().getWindow());
            logIn.initModality(Modality.WINDOW_MODAL);
            logIn.show();
            this.logInController = fxmlLoader.getController();
            logInController.setMessage(message);
            logInController.setScene(logIn);
        } else {
            System.out.println("Connect... \nTrust me. I try to connect");
        }
    }

    private Settings getSettings(){
        try {
            Settings settings;
            FileInputStream in = new FileInputStream("settings.cfg");
            ObjectInputStream deserialize = new ObjectInputStream(in);
            settings = (Settings) deserialize.readObject();
            deserialize.close();
            in.close();
            return settings;
        } catch (Exception ex) {
            return new Settings();
        }
    }

    private HBox itemToListItem(Item item){
        return new HBoxItem(item);
    }

    private HBox groupToListItem(Group group){
        return new HBoxGroup(group);
    }
}
