package sample;

import GUI.HBoxGroup;
import GUI.HBoxItem;
import com.jfoenix.controls.JFXListView;
import entites.Group;
import entites.Item;
import entites.Settings;
import entites.ShopingList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
    private Button cancelButton;
    @FXML
    JFXListView<HBox> listView;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.settings = getSettings();
        this.connect = new LocalConnect();
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
            System.out.println("Слушатели на итемы не реализованы");
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
