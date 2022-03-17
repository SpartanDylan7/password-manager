package org.eagle.passwordmanager.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.eagle.passwordmanager.App;
import org.eagle.passwordmanager.model.Login;
import org.eagle.passwordmanager.util.DatabaseManager;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public final class MainController extends BaseController {
    @FXML
    private TextField userName;
    @FXML
    private TextField pass;
    @FXML
    private TextField website;
    @FXML
    private TextArea notes;
    @FXML
    private Text name;
    @FXML
    private GridPane gridPane;
    @FXML
    private Button editBtn;
    @FXML
    private ListView<Login> itemListView;
    @FXML
    private TextField searchField;

    @FXML
    private Label leftMsg;
    @FXML
    private Label rightMsg;

    private ObservableList<Login> logins;
    private SortedList<Login> sortedLogins;
    private final BooleanProperty showLoginDetail = new SimpleBooleanProperty(false);
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();


    @FXML
    private void initialize() {

        // clear login detail area
        showLoginDetails(null);
        leftMsg.setText("");
        rightMsg.setText("");

        logins = FXCollections.observableArrayList(Login.extractor);
        logins.addAll(user.getUserItems());
//Creates a sorted list of login items that are sorted alphabetically
        sortedLogins = new SortedList<>(logins, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        initSearchField();
        initPasswordTextField();
        itemListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            clearClipboard();
            showLoginDetails(newValue);

        }));


        // select the first item of the list
        itemListView.getSelectionModel().selectFirst();

        // adding context menu to our ListView
        //Opens the URL that you gave for a password
        MenuItem openInBrowser = new MenuItem("Open in Browser");
        openInBrowser.setOnAction((event) -> {
            openURL();
        });
        //Edits an item
        MenuItem editLogin = new MenuItem("Edit");
        editLogin.setOnAction((this::editItem));
        MenuItem deleteLogin = new MenuItem("Delete");
        //Deletes an item
        deleteLogin.setOnAction((event) -> {
            if (confirmDeletion()) {
                Login currentLogin = getLogin();
                DatabaseManager.deleteItem(currentLogin);
                logins.remove(getLogin());
            }
        });
        ContextMenu contextMenu = new ContextMenu(openInBrowser, editLogin, deleteLogin);

        // remove context menu from ListView if list is empty
        itemListView.contextMenuProperty().bind(Bindings.when(Bindings.size(itemListView.itemsProperty().get()).lessThan(1))
                .then((ContextMenu) null).otherwise(contextMenu));
        website.setContextMenu(contextMenu);

        // copy userName to clipboard
        userName.setOnMousePressed((event) -> {
            userName.selectAll();
            content.putString(userName.getText());
            clipboard.setContent(content);
            leftMsg.setText("User name copied to clipboard");

        });
        //copy URL to clipboard
        website.setOnMousePressed((event) -> {
            website.selectAll();
            content.putString(website.getText());
            clipboard.setContent(content);
            leftMsg.setText("Website copied to clipboard");
        });

        gridPane.visibleProperty().bind(showLoginDetail);
        notes.visibleProperty().bind(showLoginDetail);
        editBtn.visibleProperty().bind(showLoginDetail);

    }


    /**
     * Display an Alert box to allow user to confirm deletion
     *
     * @return
     */
    private boolean confirmDeletion() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setContentText("Are you sure you want to delete this item?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(App.class.getResource("css/myDialogs.css")).toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;

    }

    /**
     * Opens the URL for an item
     */
    private void openURL() {
        Login login = getLogin();
        if (login != null) {
            try {
                Desktop.getDesktop().browse(new URL(login.getURL()).toURI());
            } catch (IOException | URISyntaxException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid URL");
                alert.setHeaderText(e.getMessage());
                alert.setContentText("Oops... Please check the URL.");
                alert.showAndWait();

            }
        }
    }

    /**
     * Gets the selected login from ListView
     *
     * @return
     */
    public Login getLogin() {
        return itemListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Clears the users clipboard
     */
    private void clearClipboard() {
        leftMsg.setText("");
        clipboard.clear();
    }

    /**
     * Displays the options "Reveal" and "Copy" when you right-click on a password field
     */
    private void initPasswordTextField() {
        //password context menu
        ContextMenu passwordContextMenu = new ContextMenu(new MenuItem("Copy"),
                new MenuItem("Reveal"));
        passwordContextMenu.setOnAction(event -> {
            Login selectedItem = getLogin();
            if ("Reveal".equals(((MenuItem) event.getTarget()).getText())) {
                pass.setText(getLogin().getPassword());
            }
            if ("Copy".equals(((MenuItem) event.getTarget()).getText())) {
                content.putString(getLogin().getPassword());
                clipboard.setContent(content);
                leftMsg.setText("Password copied to clipboard");
            }
        });
        pass.setContextMenu(passwordContextMenu);

        pass.focusedProperty().addListener((event) -> {
            if (!pass.isFocused()) {
                pass.setText("****");
            }
        });
    }

    /**
     * Displays the information for an item
     *
     * @param login
     */
    private void showLoginDetails(Login login) {
        if (login != null) {
            name.setText(login.getName());
            userName.setText(login.getUserName());
            pass.setText("****");
            website.setText(login.getURL());
            notes.setText(login.getNotes());
            showLoginDetail.setValue(true);
        } else {
            name.setText("");
            userName.setText("");
            pass.setText("");
            website.setText("");
            notes.setText("");
            showLoginDetail.setValue(false);
        }
    }


    private void initSearchField() {

        searchField.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchField.clear();
            }

        });
        //Selects all the text in a search field
        searchField.setOnMousePressed((event) -> {
            searchField.selectAll();
        });
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchField.selectAll();
            }

        });

        // Login items filtered list
        FilteredList<Login> filteredList = new FilteredList<>(sortedLogins, data -> true);
        itemListView.setItems(filteredList);
        searchField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate(data -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseSearch = newValue.toLowerCase();
                return data.getName().toLowerCase().contains(lowerCaseSearch);
            });
        }));

    }

    /**
     * When you click on Edit you can edit the information if you don't select a password then
     * it gives you a message to "Please select the login you want to edit."
     *
     * @param actionEvent
     */
    @FXML
    private void editItem(ActionEvent actionEvent) {
        Login selectedLogin = getLogin();
        if (selectedLogin == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Login Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the login you want to edit.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(name.getScene().getWindow());
        dialog.setTitle("Edit Login");
        FXMLLoader fxmlLoader = getFxmlResource("LoginEditDialog.fxml");
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        ButtonType okButton = ButtonType.OK;
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        LoginEditController contactController = fxmlLoader.getController();
        contactController.editLogin(selectedLogin);

        // disable ok button if name field is empty
        dialog.getDialogPane().lookupButton(ButtonType.OK)
                .disableProperty()
                .bind(contactController.inputsFullBinding().not());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            contactController.updateLogin(selectedLogin);
            showLoginDetails(selectedLogin);
        }
    }

    /**
     * Adds a new password to your list of passwords
     *
     * @param actionEvent
     */
    @FXML
    private void addItem(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(name.getScene().getWindow());
        dialog.setTitle("Add New Login");
        FXMLLoader fxmlLoader = getFxmlResource("LoginEditDialog.fxml");
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        LoginEditController contactController = fxmlLoader.getController();
        // disable ok button if name field is empty
        dialog.getDialogPane().lookupButton(ButtonType.OK)
                .disableProperty()
                .bind(contactController.inputsFullBinding().not());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Login newLogin = contactController.getNewLogin();

            logins.add(newLogin);
            DatabaseManager.insertItem(user.getUserId(), newLogin);

        }


    }

    /**
     * A helper method that returns a FXMLLoader
     *
     * @param fxmlResource
     * @return
     */
    private FXMLLoader getFxmlResource(String fxmlResource) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(App.class.getResource(fxmlResource));
        return fxmlLoader;
    }


}
