package org.eagle.passwordmanager.controller;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.eagle.passwordmanager.model.Login;
import org.eagle.passwordmanager.util.DatabaseManager;
import org.eagle.passwordmanager.util.PasswordGenerator;

public class LoginEditController extends BaseController {

    @FXML
    private CheckBox useDigits;
    @FXML
    private CheckBox specialChars;
    @FXML
    private Slider passLenSlider;
    @FXML
    private TextField passLen;
    @FXML
    private TextField userName;
    @FXML
    private TextField pass;
    @FXML
    private TextField website;
    @FXML
    private TextArea notes;
    @FXML
    private TextField name;

    private BooleanBinding inputsFull;

    @FXML
    private void initialize() {
        // TODO Load user password preferences
        // load user password preferences
//        useDigits.setSelected();

        // binding slider and password length text field
        passLen.textProperty().bindBidirectional(passLenSlider.valueProperty(), new StringConverter<>() {
            @Override
            public String toString(Number object) {

                return String.valueOf(object.intValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (Exception ignored) {
                    return 10;
                }

            }
        });

        // here we bind name TextField for validation. We want to disable the Ok button if name field is empty
        inputsFull = new BooleanBinding() {
            {
                bind(name.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return !(name.getText().trim().isEmpty());
            }
        };
    }

    public final boolean getInputsFull() {
        return inputsFull.get();
    }

    public BooleanBinding inputsFullBinding() {
        return inputsFull;
    }

    public Login getNewLogin() {
        Login login = new Login(name.getText());
        login.setUserName(userName.getText());
        login.setPassword(pass.getText());
        login.setURL(website.getText());
        login.setNotes(notes.getText());
        return login;
    }

    public void editLogin(Login selectedLogin) {
        name.setText(selectedLogin.getName());
        userName.setText(selectedLogin.getUserName());
        pass.setText(selectedLogin.getPassword());
        website.setText(selectedLogin.getURL());
        notes.setText(selectedLogin.getNotes());

    }

    public void updateLogin(Login selectedLogin) {
        selectedLogin.setName(name.getText().trim());
        selectedLogin.setUserName(userName.getText().trim());
        selectedLogin.setPassword(pass.getText());
        selectedLogin.setURL(website.getText().trim());
        selectedLogin.setNotes(notes.getText());
        DatabaseManager.updateItem(selectedLogin);
    }

    @FXML
    private void generatePassword(ActionEvent event) {
        pass.setText(PasswordGenerator.getInstance().generate((int) passLenSlider.getValue(), specialChars.isSelected(), useDigits.isSelected()));
    }


}
