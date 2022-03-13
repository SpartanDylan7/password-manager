package org.eagle.passwordmanager.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.eagle.passwordmanager.ViewFactory;
import org.eagle.passwordmanager.util.DatabaseManager;
import org.eagle.passwordmanager.model.User;
import org.eagle.passwordmanager.util.PasswordAuthentication;
import org.eagle.passwordmanager.util.SecretsManager;
import org.eagle.passwordmanager.util.UsersManager;

public class LoginController extends BaseController {
    @FXML
    private Label message;
    @FXML
    private Hyperlink newAccountLink;
    @FXML
    private Button loginBtn;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField passwordField;
    private final BooleanProperty lockedOut = new SimpleBooleanProperty(false);
    private int failedLoginAttempts = 0;


    @FXML
    private void initialize() {
        message.setText("");
        loginBtn.disableProperty().bind(Bindings.isEmpty(userName.textProperty()).or(passwordField.textProperty().isEmpty()).or(lockedOut));
        newAccountLink.disableProperty().bind(lockedOut);
    }

    public void newAccount(MouseEvent mouseEvent) {
//        System.out.println("new account request");
        message.setText("");
        userName.clear();
        passwordField.clear();
        ViewFactory.getInstance().showNewUserWindow();

    }

    @FXML
    private void onLogin(ActionEvent event) {
        User authUser = DatabaseManager.getUser(userName.getText());
        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
        boolean isValidUser = passwordAuthentication.authenticate(passwordField.getText().toCharArray(), authUser.getAuthToken());
        if (isValidUser) {
            message.setText("Welcome");
            user = authUser;
            UsersManager.getInstance().setCurrentUser(authUser);
            // decrypt user's items
            user.getUserItems().forEach(SecretsManager::decryptLogin);
            //password hash should be set, and we can clear user password
            user.setPassword("");
            failedLoginAttempts = 0;
            lockedOut.setValue(false);
            Stage stage = (Stage) message.getScene().getWindow();

            ViewFactory.getInstance().showMainWindow();
            ViewFactory.getInstance().closeStage(stage);
        } else {

            message.setText("Invalid user name or password");
            // button will be disabled for 10+ seconds if failed to login three in a row
            if (++failedLoginAttempts >= 3) {
                System.out.println("locked");
                lockedOut.setValue(true);
                message.setText("Please wait...");
                new Thread(() -> {
                    try {
                        Thread.sleep(10_000L * (failedLoginAttempts - 2));
                        Platform.runLater(() -> {
                            lockedOut.setValue(false);
                            message.setText("");
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();


            }
        }

    }

}
