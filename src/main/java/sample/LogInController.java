package sample;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable {
    private Stage stage;
    private double xOffset, yOffset;
    @FXML
    private Label message;
    @FXML
    private Button cancelButton;
    @FXML
    private Button logInButton;
    @FXML
    private JFXTextField logInField;
    @FXML
    private JFXTextField passwordField;
    private Controller parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancelButton.setOnAction(actionEvent -> {
            ((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).close();
        });
        logInButton.setOnAction(action ->{
            parentController.setUserData(logInField.getText(), passwordField.getText());
            parentController.connect();
            ((Stage)((Button)action.getSource()).getScene().getWindow()).close();
        });
    }

    public void setMessage(String message){
        this.message.setText(message);
        if(message != null){
            this.message.setVisible(true);
        }
    }

    public void setScene(Stage stage){
        this.stage = stage;
        stage.getScene().setOnMousePressed(mouseEvent -> {
            xOffset = stage.getX() - mouseEvent.getScreenX();
            yOffset = stage.getY() - mouseEvent.getScreenY();
        });
        stage.getScene().setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + xOffset);
            stage.setY(mouseEvent.getScreenY() + yOffset);
        });
    }

    public void setParentController(Controller controller) {
        this.parentController = controller;
    }
}
