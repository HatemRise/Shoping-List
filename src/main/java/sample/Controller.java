package sample;

import GUI.ShopingListCell;
import GUI.ShopingListGroupCell;
import GUI.ShopingListView;
import com.jfoenix.controls.*;
import entites.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import server.Connection;
import entites.ShopingList;
import server.ShopingNet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.List;
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
    private AnchorPane mainPane;
    @FXML
    private ShopingListView listView;
    @FXML
    private JFXComboBox<Link> listLinks;
    /**Метод инициализации конструктора. Вызывается при инициализации в методе main()
     * на 19 строке(fxmlLoader.load())*/
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.settings = getSettings();
        this.connect = new ShopingNet();
        itemPrioritySwitcher.getItems().addAll(Priority.values());
        itemPrioritySwitcher.setPromptText("Priority");
        if(this.settings.getUser() != null) {
            listLinks.getItems().addAll(this.settings.getUser().getLists());
        }
        testDataList();
        listView.selectedProperty().addListener(action -> {
            if (listView.getSelected() != -1) {
                selectedItemProperty(false);
            } else {
                selectedItemProperty(true);
            }
        });
    }
    /**Метод, который слишком надолго прижился в этой проге.
     * Заполняет LitView тестовыми данными. Будет удалён, когда будет
     * возможность сделать полноценный лист*/
    private void testDataList(){
        list = new ShopingList("test");
        for(int i = 0 ; i < 3; i++) {
            Item item = new Item("name" + i);
            item.setQuantity(12 + i);
            item.setDescription("description" + i);
            list.getList().add(item);
        }
        for(int j = 0 ; j < 3; j++) {
            Group group = new Group("name" + String.valueOf(j));
            for (int i = 0; i < 3; i++) {
                Item item = new Item("groupItem" + String.valueOf(i + j));
                item.setQuantity(12 + i);
                item.setDescription("description" + String.valueOf(i + j));
                item.setGroup(group);
                list.getList().add(item);
            }
        }
        listView.getItems().addAll(list.getList());
    }
    /**Метод вешает слуштели на обёрнутый объект Item.
     * На вход принимает обёрнутый Item HBoxItem*/
    private void selectedItemProperty(boolean enable){
        itemPropertiesPane.setDisable(enable);
        Item item = listView.getSelected() != -1 ? listView.getSelectedItem() : new Item("NONE");
        ShopingListCell cell = listView.getSelected() != -1 ? listView.getSelectedCell() : null;
        itemName.setText(item.getName());
        itemName.setOnAction(actionEvent -> {
            item.setName(itemName.getText());
            cell.getName().setText(itemName.getText());
        });
        itemQuantityMinus.setOnAction(action -> {
            if(Integer.parseInt(itemQuantity.getText()) > 0) {
                item.setQuantity(item.getQuantity() - 1);
                cell.getQuantity().setText(String.valueOf(item.getQuantity()));
                itemQuantity.setText(String.valueOf(item.getQuantity()));
            }
        });
        itemQuantity.setText(String.valueOf(item.getQuantity()));
        itemQuantity.setOnAction(action -> {
            item.setQuantity(itemQuantity.getText());
            cell.getQuantity().setText(String.valueOf(item.getQuantity()));
        });
        itemQuantityPlus.setOnAction(action -> {
            item.setQuantity(item.getQuantity() + 1);
            cell.getQuantity().setText(String.valueOf(item.getQuantity()));
            itemQuantity.setText(String.valueOf(item.getQuantity()));
        });
        itemPrioritySwitcher.setOnAction(action -> {
            if(itemPrioritySwitcher.getValue() != item.getPriority()) {
                item.setPriority(itemPrioritySwitcher.getValue());
            }
        });
        itemPrioritySwitcher.setValue(item.getPriority());
        itemDescription.setText(item.getDescription());
        itemDescriptionSaveButton.setDisable(true);
        itemDescription.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                itemDescriptionSaveButton.setDisable(false);
            }
        });
        itemDescriptionSaveButton.setOnAction(action ->{
            item.setDescription(itemDescription.getText());
            cell.getDescription().setText(itemDescription.getText());
            itemDescriptionSaveButton.setDisable(true);
        });
        itemRemoveButton.setOnAction(action -> {
            list.remove(item);
            listView.getItems().remove(item);
            listView.setSelected(-1);
        });

    }
    /**Метод для вызова логин окна, или метода для коннекта,
     * в зависимости от нличия пользовательских данных*/
    public void logIn() throws IOException {
        if(emptyUser()) {
            showLogInWindow(null);
        } else {
            connect();
        }
    }
    /**Метод для закрытия главного окна(приводит к выходу из приложения)*/
    public void exit(){
        primaryStage.close();
    }
    /**Метод для "выхода из системы". На само деле,
     * мы просто удаляет пользовательские данные
     * из объекта Settings*/
    public void logOut(){
        this.settings.setUser(null);
    }
    /**Метод для сохранения объекта ShopingList локально*/
    public void save(){
        System.out.println("Save Не реализовано!");
    }
    /**Метод для сохранения объекта ShopingList удалённо.
     * Перед сохранением следует проверить, есть ли ссылка на этот
     * файл. В случае, если такой файл уже есть в репозитории,
     * то следует его заменить методом change в интерфейсе connect.
     * Если же файла нет, то следует его создать методом create
     * в интерфейсе connect*/
    public void saveRemote(){
        if(emptyUser()){
            showLogInWindow("Please log in for continue");
            return;
        } else {
            connect.create(this.settings.getUser(), list);
        }
    }
    VBox addSelectRoot = new VBox();
    public void addSelect(Event event){
        if(mainPane.getChildren().contains(addSelectRoot)){
            return;
        }
        JFXButton trigger = (JFXButton)event.getTarget();
        JFXButton addItem = new JFXButton("Item");
        JFXButton addGroup = new JFXButton("Group");
        addItem.setPrefSize(trigger.getWidth(), trigger.getHeight());
        addGroup.setPrefSize(trigger.getWidth(), trigger.getHeight());
        addItem.setFont(new Font("System Bold", 24));
        addGroup.setFont(new Font("System Bold", 24));
        addSelectRoot.getChildren().addAll(addItem,addGroup);
        addSelectRoot.setLayoutY(trigger.getLayoutY() - trigger.getHeight() / 2);
        addSelectRoot.setLayoutX(trigger.getPrefWidth());
        mainPane.getChildren().add(addSelectRoot);
        addItem.setOnAction(actionEvent -> {
            addSelectRoot.getChildren().clear();
            addItem();
        });
        addGroup.setOnAction(actionEvent -> {
            addSelectRoot.getChildren().clear();
            addGroup();
        });
        addSelectRoot.setOnMouseExited(action -> {
            mainPane.getChildren().remove(addSelectRoot);
            addSelectRoot.getChildren().clear();
        });
    }

    public void addItem(){
        listView.getItems().add(0, new Item(""));
        listView.setSelected(0);
    }

    public  void addGroup(){
        listView.getCells().add(0, new ShopingListGroupCell("Input your name"));
    }
    /**Метод для получения объекта ShopingList по ссылке, и последующем
     * отображении в таблице*/
    public void getLists(){
        if(emptyUser()){
            showLogInWindow("Please log in for continue");
            return;
        } else {
//            connect.getList(settings.getUser(), )
        }
    }
    /**Метод для удаления файла из удалённого репозитория по ссылке*/
    public void deleteRemoteFile(){
        if(emptyUser()){
            showLogInWindow("Please log in for continue");
            return;
        } else {
//            connect.delete(settings.getUser(), )
        }
    }
    /**Метод для ресета листа. Shopinglist должен удаляться из локального
     * хранилища и скачиваться заново*/
    public void deleteAndDownload(){
        if(emptyUser()){
            showLogInWindow("Please log in for continue");
            return;
        } else {
//            connect.delete(settings.getUser(), )
        }
    }
    VBox deleteSelectRoot = new VBox();
    public void deleteSelect(Event event){
        if(mainPane.getChildren().contains(deleteSelectRoot)){
            return;
        }
        JFXButton trigger = (JFXButton)event.getTarget();
        JFXButton deleteRemote = new JFXButton("Remote");
        JFXButton deleteLocal = new JFXButton("Local");
        JFXButton deleteRemoteAndLocal = new JFXButton("All");
        deleteRemote.setPrefSize(trigger.getWidth(), trigger.getHeight());
        deleteLocal.setPrefSize(trigger.getWidth(), trigger.getHeight());
        deleteRemoteAndLocal.setPrefSize(trigger.getWidth(), trigger.getHeight());
        deleteLocal.setFont(new Font("System Bold", 24));
        deleteRemote.setFont(new Font("System Bold", 24));
        deleteRemoteAndLocal.setFont(new Font("System Bold", 24));
        deleteSelectRoot.getChildren().addAll(deleteLocal,deleteRemote,deleteRemoteAndLocal);
        deleteSelectRoot.setLayoutY(trigger.getLayoutY() - trigger.getHeight());
        deleteSelectRoot.setLayoutX(trigger.getPrefWidth());
        mainPane.getChildren().add(deleteSelectRoot);
        deleteLocal.setOnAction(actionEvent -> {
            deleteSelectRoot.getChildren().clear();
        });
        deleteRemote.setOnAction(actionEvent -> {
            deleteSelectRoot.getChildren().clear();
        });
        deleteRemoteAndLocal.setOnAction(actionEvent -> {
            deleteSelectRoot.getChildren().clear();
        });
        deleteSelectRoot.setOnMouseExited(action -> {
            mainPane.getChildren().remove(deleteSelectRoot);
            deleteSelectRoot.getChildren().clear();
        });
    }

    /**Метод для получения изменений из удалённой версии редактируемого
     * листа. Пока остаётся в виде заглушки, ибо логика для него ещё не
     * прописана в классах-наследниках Entity*/
    public void getChange(){
        System.out.println("Get change не реализовано");
    }

    public void sharedList(){
        if(emptyUser()){
            showLogInWindow("Please log in for continue");
            return;
        }
        VBox root = new VBox();
        HBox buttonPane = new HBox();
        Stage link = new Stage();
        Scene scene = new Scene(root, 300, 90);
        JFXButton cancel = new JFXButton("Cancel");
        cancel.setOnAction(action ->{link.close();});
        JFXButton add = new JFXButton("add");
        add.setPrefWidth(60);
        JFXTextField linkField = new JFXTextField();
        Region space = new Region();
        space.setPrefWidth(170);
        cancel.getStyleClass().addAll("cancelButton", "linkButton");
        add.getStyleClass().addAll("addButton", "linkButton");
        buttonPane.getStyleClass().add("buttonPane");
        linkField.setPromptText("Link");
        linkField.setLabelFloat(true);
        linkField.getStyleClass().add("linkField");
        root.getStyleClass().add("linkBody");
        root.getStylesheets().add("/Style.css");
        buttonPane.getChildren().addAll(add, space, cancel);
        root.getChildren().addAll(linkField, buttonPane);
        link.setScene(scene);
        link.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        link.setTitle("Log In");
        add.setOnAction(action ->{connect.addToMyLists(settings.getUser(), new Link(linkField.getText()));});
        link.initOwner(primaryStage.getScene().getWindow());
        link.initModality(Modality.WINDOW_MODAL);
        link.show();
    }
    /**Метод для сворчивания окна приложения*/
    public void minimizeWindow(){
        primaryStage.setIconified(true);
    }
    /**Метод для проверки на наличие пользовательских данных.
     * Если User не задан(равен null), то метод вернёт true*/
    private boolean emptyUser(){
        return settings.getUser() == null ? true : false;
    }
    /**Костыль для получения primaryStage в конструкторе до отображения интерфейса.
     * Так же добавления слушателя на мышь. Если пользователь нажал мышкой и потащил
     * окно, то оно должно следовать за мышью*/
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
        showLogInWindow(null);
    }
    /**Метод для получения с сервера листа с сылками на наши файлы.
     * Если сервер навернётся и ничего не вернёт, то мы об этом даже
     * не узнаем. Держу в курсе*/
    public void connect(){
        List<Link> response = connect.logIn(this.settings.getUser());
        this.settings.getUser().setLists(response);
        listLinks.getItems().addAll(response);
        System.out.println(response.get(0).toString());
    }
    /**Очередной костыль. На этот раз уже для получения данных из
     * Логин формы при нажатии на кнопку Log In*/
    public void setUserData(String name, String password){
        if(this.settings.getUser() != null) {
            this.settings.getUser().setName(name);
            this.settings.getUser().setPassword(password);
        }else{
            this.settings.setUser(new User(name, password));
        }
    }

    /**Метод для отображения модыльного окна авторизации
     * в качестве параметра принимает сообщение с ошибкой,
     * если такое имеется. В иных же случаях, следует
     * передавать null в качестве аргумента*/
    private void showLogInWindow(String message) {
        try {
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
            logInController.setParentController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Метод для получения настроек из локального файла
     * если такого файла мы не нашли, то возвращаем
     * новый объект с настройками*/
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
}
