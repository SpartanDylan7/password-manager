package org.eagle.passwordmanager.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.eagle.passwordmanager.ViewFactory;
import org.eagle.passwordmanager.util.DatabaseManager;
import org.eagle.passwordmanager.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserController extends BaseController {
    @FXML
    private Label message;

    @FXML
    private TextField userFld;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField emailFld;
    @FXML
    private PasswordField passFld;
    @FXML
    private PasswordField passConfFld;
    private final BooleanProperty passwordMismatch = new SimpleBooleanProperty(true);
    private final int MIN_PASS_LENGTH = 2;


    @FXML
    private void initialize() {

        message.setText("");
        passFld.setTooltip(new Tooltip("Password must be at least 6 characters long\n" +
                "Password must contain at least one letter"));
        saveBtn.disableProperty().bind(Bindings.isEmpty(userFld.textProperty()).
                or(passFld.textProperty().isEmpty()).or(passwordMismatch));
        passwordMismatch.bind(Bindings.equal(passFld.textProperty(), passConfFld.textProperty()).not()
                .or(Bindings.greaterThanOrEqual(MIN_PASS_LENGTH, passFld.textProperty().length())));
        // clear message field if email text changes
        emailFld.textProperty().addListener(observable -> {
            message.setText("");
        });
        // clear message field if user field text changes
        userFld.textProperty().addListener(observable -> {
            message.setText("");
        });

    }

    @FXML
    private void onSave(ActionEvent event) {

        User user = new User(userFld.getText(), passFld.getText());
        if (!emailFld.getText().isEmpty()) {
            if (validateEmail()) {
                user.setEmail(emailFld.getText());
            } else {
                event.consume();
                message.setText("Invalid email");
                return;
            }
        }

//        boolean result = DatabaseManager.insertNewUser(user);
        boolean result = DatabaseManager.insertNewUser(user);
        if(!result){
            message.setText("Please select different user name");

            event.consume();
            return;
        }
//        System.out.println("User id: "+user.getUserId());


        Stage stage = (Stage) message.getScene().getWindow();
        ViewFactory.getInstance().closeStage(stage);

    }

    private boolean validateEmail() {
        //Regular expression to accept valid email id
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        //Creating a pattern object
        Pattern pattern = Pattern.compile(regex);
        //Creating a Matcher object
        Matcher matcher = pattern.matcher(emailFld.getText());
        return matcher.matches();
    }

    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = (Stage) message.getScene().getWindow();
        ViewFactory.getInstance().closeStage(stage);
    }
}
