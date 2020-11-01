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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
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

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
    @FXML
    private JFXComboBox<Group> itemGroupSwitcher;
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
        listView.setContextEvent(action -> itemContext(action));
        listView.setContextGroupEvent(action -> groupContext(action));
        listView.selectedProperty().addListener(action -> {
            if (listView.getSelected() != -1) {
                selectedItemProperty(false);
            } else {
                selectedItemProperty(true);
            }
        });
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
            cell.changeHashCode(item.hashCode());
        });
        itemQuantityMinus.setOnAction(action -> {
            if(Integer.parseInt(itemQuantity.getText()) > 0) {
                item.setQuantity(item.getQuantity() - 1);
                cell.getQuantity().setText(String.valueOf(item.getQuantity()));
                cell.changeHashCode(item.hashCode());
                itemQuantity.setText(String.valueOf(item.getQuantity()));
            }
        });
        itemQuantity.setText(String.valueOf(item.getQuantity()));
        itemQuantity.setOnAction(action -> {
            item.setQuantity(itemQuantity.getText());
            cell.getQuantity().setText(String.valueOf(item.getQuantity()));
            cell.changeHashCode(item.hashCode());
        });
        itemQuantityPlus.setOnAction(action -> {
            item.setQuantity(item.getQuantity() + 1);
            cell.getQuantity().setText(String.valueOf(item.getQuantity()));
            itemQuantity.setText(String.valueOf(item.getQuantity()));
            cell.changeHashCode(item.hashCode());
        });
        itemPrioritySwitcher.setOnAction(action -> {
            if(itemPrioritySwitcher.getValue() != item.getPriority()) {
                item.setPriority(itemPrioritySwitcher.getValue());
            }
        });
        itemGroupSwitcher.setOnAction(action -> {
            if(itemGroupSwitcher.getValue() != item.getGroup()) {
                if (itemGroupSwitcher.getValue().getName().equals("Empty")) {
                    if(item.getGroup() != null) {
                        listView.groupCounterSubtract(item.getGroup().getName());
                        item.setGroup(null);
                        listView.groupChange(item);
                    }
                } else {
                    if(item.getGroup() != null) {
                        listView.groupCounterSubtract(item.getGroup().getName());
                    }
                    item.setGroup(itemGroupSwitcher.getValue());
                    listView.groupChange(item);
                }
            }
        });
        itemGroupSwitcher.setValue(item.getGroup() != null ? item.getGroup()
                : itemGroupSwitcher.getItems().get(0));
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
            cell.changeHashCode(item.hashCode());
            itemDescriptionSaveButton.setDisable(true);
        });
        itemRemoveButton.setOnAction(action -> {
            list.remove(item);
            listView.getItems().remove(item);
            listView.setSelected(-1);
        });

    }
    ContextMenu menu = new ContextMenu();
    private void groupContext(ContextMenuEvent event){
        menu.hide();
        menu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        MenuItem edit = new MenuItem("Edit");
        menu.getStyleClass().addAll("contextMenu");
        remove.getStyleClass().addAll("contextMenuItem");
        edit.getStyleClass().addAll("contextMenuItem");
        remove.setOnAction(action -> {
            listView.getCells().parallelStream().filter(cell -> cell.getGroupName() != null && cell.getGroupName().equals(((ShopingListGroupCell)event.getSource()).getName().getText()))
                    .forEach(cell -> {
                        cell.setGroupName(null);
                    });
            List<Item> temp = listView.getItems().parallelStream().filter(item -> item.getGroup() != null
                    && item.getGroup().getName().equals(((ShopingListGroupCell)event.getSource()).getName().getText())).collect(Collectors.toList());
            temp.stream().forEachOrdered(item -> {
                list.getGroups().remove(item.getGroup());
                itemGroupSwitcher.getItems().remove(item.getGroup());
                item.setGroup(null);
                listView.groupChange(item);
            });
            listView.getCells().remove(event.getSource());
        });
        edit.setOnAction(action -> {
            editGroup((ShopingListGroupCell) event.getSource());
        });
        menu.setHideOnEscape(true);
        menu.getItems().addAll(edit, remove);
        menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
    }

    private void itemContext(ContextMenuEvent event){
        menu.hide();
        menu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        menu.getStyleClass().addAll("contextMenu");
        remove.getStyleClass().addAll("contextMenuItem");
        remove.setOnAction(action -> {
            listView.getItems().remove(listView.getSelectedItem());
        });
        menu.getItems().add(remove);
        menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
    }
    /**Метод для вызова логин окна, или метода для коннекта,
     * в зависимости от нличия пользовательских данных*/
    public void logIn() {
        listLinks.getItems().clear();
        if(emptyUser()) {
            showLogInWindow(null);
        } else {
            connect();
        }
    }

    private void getLocalLinks() {
        File folder = new File("/lists/" + settings.getUser().getName());
        if(folder.list() != null) {
            List<String> links = Arrays.asList(folder.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".shllf");
                }
            }));
            links.parallelStream().forEach(link -> {
                settings.getUser().addLink(new Link(null, (folder.getPath() + link), link.substring(0, link.length() - 6)));
            });
            listLinks.getItems().addAll(settings.getUser().getLists());
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
        this.listLinks.getItems().clear();
        this.list = null;
        this.listView.getItems().clear();
        this.itemGroupSwitcher.getItems().clear();
    }
    public void save(Event event){
        if(this.settings.getUser() == null){
            showLogInWindow("Please log in for continue");
            return;
        }
        saveLocal();
        saveRemote();
    }

    public void saveLocal() {
        File localShopingListFile = new File("/lists/" + settings.getUser().getName() + "/" + list.getName() + ".shllf");
        try {
            FileOutputStream localStorage = new FileOutputStream(localShopingListFile);
            ObjectOutputStream listObject = new ObjectOutputStream(localStorage);
            listObject.writeObject(list);
            listObject.flush();
            listObject.close();
            localStorage.flush();
            localStorage.close();
            this.settings.getUser().getList(this.settings.getUser().getLists()
                    .indexOf(listLinks.getValue())).setLocal(localShopingListFile.getPath());
            listLinks.getValue().setLocal(localShopingListFile.getPath());
        } catch (FileNotFoundException e){
            try {
                localShopingListFile.createNewFile();
                saveLocal();
            }catch (IOException ex){
                File folder = new File("/lists");
                folder.mkdir();
                saveLocal();
            }
        } catch (IOException e) {
            System.out.println("IO Error: Object stream not created!");
        }
    }

    public void addList(){
        VBox root = new VBox();
        HBox buttonPane = new HBox();
        Stage group = new Stage();
        Scene scene = new Scene(root, 300, 90);
        JFXButton cancel = new JFXButton("Cancel");
        cancel.setOnAction(action ->{group.close();});
        JFXButton add = new JFXButton("add");
        add.setPrefWidth(60);
        JFXTextField shopingListName = new JFXTextField();
        Region space = new Region();
        space.setPrefWidth(170);
        cancel.getStyleClass().addAll("cancelButton", "linkButton");
        add.getStyleClass().addAll("addButton", "linkButton");
        buttonPane.getStyleClass().add("buttonPane");
        shopingListName.setPromptText("Link");
        shopingListName.setLabelFloat(true);
        shopingListName.getStyleClass().add("linkField");
        root.getStyleClass().add("linkBody");
        root.getStylesheets().add("/Style.css");
        buttonPane.getChildren().addAll(add, space, cancel);
        root.getChildren().addAll(shopingListName, buttonPane);
        group.setScene(scene);
        group.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        group.setTitle("Log In");
        group.initOwner(primaryStage.getScene().getWindow());
        group.initModality(Modality.WINDOW_MODAL);
        group.show();
        add.setOnAction(action -> {
            if(shopingListName.getText().length() < 1){

            } else {
                this.list = new ShopingList(shopingListName.getText());
                Link link = new Link();
                link.setName(list.getName());
                this.settings.getUser().addLink(link);
                this.listLinks.getItems().addAll(link);
                this.listLinks.setValue(link);
                this.listView.clear();
                this.listView.getItems().addAll(list.getList());
                itemGroupSwitcher.getItems().clear();
                itemGroupSwitcher.getItems().add(new Group("Empty"));
                itemGroupSwitcher.getItems().addAll(list.getGroups());
                group.close();
            }
        });
    }

    public void share(){
        VBox root = new VBox();
        HBox buttonPane = new HBox();
        Stage group = new Stage();
        Scene scene = new Scene(root, 300, 90);
        JFXButton cancel = new JFXButton("Cancel");
        cancel.setOnAction(action ->{group.close();});
        JFXTextField link = new JFXTextField(listLinks.getValue().getRemote());
        cancel.getStyleClass().addAll("cancelButton", "linkButton");
        buttonPane.getStyleClass().add("buttonPane");
        link.setPromptText("Link");
        link.setLabelFloat(true);
        link.getStyleClass().add("linkField");
        root.getStyleClass().add("linkBody");
        root.getStylesheets().add("/Style.css");
        buttonPane.getChildren().addAll(cancel);
        root.getChildren().addAll(link, buttonPane);
        group.setScene(scene);
        group.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        group.setTitle("Log In");
        group.initOwner(primaryStage.getScene().getWindow());
        group.initModality(Modality.WINDOW_MODAL);
        group.show();
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
            if(listLinks.getValue() != null && listLinks.getValue().getRemote() != null && listLinks.getValue().getRemote().length() > 1){
                connect.change(settings.getUser(), listLinks.getValue(), list);
            } else {
                connect.create(this.settings.getUser(), list);
            }
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
        Item item = new Item("");
        if(!listView.getItems().contains(item)) {
            listView.getItems().add(0, item);
            listView.setSelected(-1);
            listView.setSelected(0);
            list.addItem(item);
        }
    }

    public  void addGroup(){
        VBox root = new VBox();
        HBox buttonPane = new HBox();
        Stage group = new Stage();
        Scene scene = new Scene(root, 300, 90);
        JFXButton cancel = new JFXButton("Cancel");
        cancel.setOnAction(action ->{group.close();});
        JFXButton add = new JFXButton("add");
        add.setPrefWidth(60);
        JFXTextField groupNameField = new JFXTextField();
        Region space = new Region();
        space.setPrefWidth(170);
        cancel.getStyleClass().addAll("cancelButton", "linkButton");
        add.getStyleClass().addAll("addButton", "linkButton");
        buttonPane.getStyleClass().add("buttonPane");
        groupNameField.setPromptText("Link");
        groupNameField.setLabelFloat(true);
        groupNameField.getStyleClass().add("linkField");
        root.getStyleClass().add("linkBody");
        root.getStylesheets().add("/Style.css");
        buttonPane.getChildren().addAll(add, space, cancel);
        root.getChildren().addAll(groupNameField, buttonPane);
        group.setScene(scene);
        group.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        group.setTitle("Log In");
        group.initOwner(primaryStage.getScene().getWindow());
        group.initModality(Modality.WINDOW_MODAL);
        group.show();
        add.setOnAction(action -> {
            if(groupNameField.getText().length() < 3){

            } else {
                listView.getCells().add(new ShopingListGroupCell(groupNameField.getText()));
                itemGroupSwitcher.getItems().add(new Group(groupNameField.getText()));
                group.close();
            }
        });
    }

    private void editGroup(ShopingListGroupCell cell){
        VBox root = new VBox();
        HBox buttonPane = new HBox();
        Stage group = new Stage();
        Scene scene = new Scene(root, 300, 90);
        JFXButton cancel = new JFXButton("Cancel");
        cancel.setOnAction(action ->{group.close();});
        JFXButton edit = new JFXButton("Edit");
        edit.setPrefWidth(60);
        JFXTextField groupNameField = new JFXTextField(cell.getName().getText());
        Region space = new Region();
        space.setPrefWidth(170);
        cancel.getStyleClass().addAll("cancelButton", "linkButton");
        edit.getStyleClass().addAll("addButton", "linkButton");
        buttonPane.getStyleClass().add("buttonPane");
        groupNameField.setPromptText("Link");
        groupNameField.setLabelFloat(true);
        groupNameField.getStyleClass().add("linkField");
        root.getStyleClass().add("linkBody");
        root.getStylesheets().add("/Style.css");
        buttonPane.getChildren().addAll(edit, space, cancel);
        root.getChildren().addAll(groupNameField, buttonPane);
        group.setScene(scene);
        group.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        group.setTitle("Log In");
        group.initOwner(primaryStage.getScene().getWindow());
        group.initModality(Modality.WINDOW_MODAL);
        group.show();
        edit.setOnAction(action -> {
            if(groupNameField.getText().length() < 3){
            } else if(!listView.isThisGroupExist(groupNameField.getText())){
                listView.getItems().parallelStream().filter(item -> item.getGroup() != null
                        && item.getGroup().getName().equals(cell.getName().getText())).forEachOrdered(item -> {
                    itemGroupSwitcher.getItems().remove(item.getGroup());
                    item.getGroup().setName(groupNameField.getText());
                });
                listView.getCells().parallelStream().filter(item -> item.getGroupName() != null
                        && item.getGroupName().equals(cell.getName().getText())).forEach(item -> {
                            item.setGroupName(groupNameField.getText());
                });
                cell.getName().setText(groupNameField.getText());
                itemGroupSwitcher.getItems().add(new Group(groupNameField.getText()));
                group.close();
            }
        });
    }
    /**Метод для получения объекта ShopingList по ссылке, и последующем
     * отображении в таблице*/
    public void getLists() throws IOException, ClassNotFoundException {
        if(emptyUser()){
            showLogInWindow("Please log in for continue");
            return;
        } else {
            if(listLinks.getValue().getRemote().isEmpty()) {
                getLocal();
                return;
            }
            File file = connect.getList(settings.getUser(), listLinks.getValue());
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream remoteList = new ObjectInputStream(in);
            this.list = (ShopingList) remoteList.readObject();
            remoteList.close();
            in.close();
            listView.clear();
            listView.getItems().addAll(list.getList());
            itemGroupSwitcher.getItems().clear();
            itemGroupSwitcher.getItems().add(new Group("Empty"));
            itemGroupSwitcher.getItems().addAll(list.getGroups());
        }
    }
    /**Метод для удаления файла из удалённого репозитория по ссылке*/
    public void deleteRemoteFile(){
        if(emptyUser()){
            showLogInWindow("Please log in for continue");
            return;
        } else {
            if(connect.delete(settings.getUser(), listLinks.getValue())){
                int index = this.settings.getUser().getLists().lastIndexOf(listLinks.getValue());
                this.settings.getUser().getList(index).setRemote(null);
                listLinks.getValue().setRemote(null);
            }
        }
    }
    /**Метод для ресета листа. Shopinglist должен удаляться из локального
     * хранилища и скачиваться заново*/
    public void deleteAndDownload(){
        if(emptyUser()){
            showLogInWindow("Please log in for continue");
            return;
        } else if(listLinks.getValue() != null && listLinks.getValue().getRemote() != null){
            File shopingList = new File(listLinks.getValue().getLocal());
            if(shopingList.exists())shopingList.delete();
            connect.getList(this.settings.getUser(), listLinks.getValue());
            saveLocal();
            getLocal();
        }
    }

    private void getLocal(){
        list = null;
        listView.clear();
        itemGroupSwitcher.getItems().clear();
        itemGroupSwitcher.getItems().add(new Group("Empty"));
        File shopingList = new File(listLinks.getValue().getLocal());
        try {
            FileInputStream in = new FileInputStream(shopingList);
            ObjectInputStream oin = new ObjectInputStream(in);
            list = (ShopingList) oin.readObject();
            listView.getItems().addAll(list.getList());
            itemGroupSwitcher.getItems().addAll(list.getGroups());
        }catch (Exception e){
            System.out.println("Error: can not get local list");
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
            deleteLocal();
        });
        deleteRemote.setOnAction(actionEvent -> {
            deleteSelectRoot.getChildren().clear();
            deleteRemoteFile();
        });
        deleteRemoteAndLocal.setOnAction(actionEvent -> {
            deleteSelectRoot.getChildren().clear();
            deleteRemoteFile();
            deleteLocal();
        });
        deleteSelectRoot.setOnMouseExited(action -> {
            mainPane.getChildren().remove(deleteSelectRoot);
            deleteSelectRoot.getChildren().clear();
        });
    }

    public void deleteLocal(){
        File shopingList = new File(listLinks.getValue().getLocal());
        shopingList.delete();
        this.settings.getUser().getLists().remove(listLinks.getValue());
        listLinks.getValue().setLocal(null);
        list = null;
        listView.clear();
        itemGroupSwitcher.getItems().clear();
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
        return this.settings.getUser() == null ? true : false;
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
        getLocalLinks();
        List<Link> response = connect.logIn(this.settings.getUser());
        this.settings.getUser().getLists().parallelStream()
                .forEach(item -> {
                    Optional<Link> temp = response.parallelStream().filter(res -> res.getName().equals(item.getName())).findFirst();
                    if(!temp.isEmpty()) item.setRemote(temp.get().getRemote());
                });
        this.settings.getUser().setLists(response);
        listLinks.getItems().addAll(response);
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
            Settings settings = new Settings();
            return settings;
        }
    }
}
