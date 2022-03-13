package org.eagle.passwordmanager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eagle.passwordmanager.controller.BaseController;
import org.eagle.passwordmanager.controller.LoginController;
import org.eagle.passwordmanager.controller.MainController;
import org.eagle.passwordmanager.controller.NewUserController;

import java.io.IOException;

public final class ViewFactory {

    private static ViewFactory INSTANCE;

//Private Constructor
    private ViewFactory() {

    }

    /**
     * Makes a ViewFactory that is a singleton- Only 1 in the entire program
     * @return
     */
    public static ViewFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewFactory();
        }
        return INSTANCE;
    }

    /**
     * Loads FMXL file and creates a stage | Each FMXL page is paired with a controller
     * @param controller
     * @param title displayed on the stage
     */
    private void initializeStage(BaseController controller, String title) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(controller.getFxmlName()));
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    /**
     * Displays the main window to the user
     */
    public void showMainWindow() {
//        System.out.println(" show main window called");
        BaseController controller = new MainController();
        controller.setFxmlName("MainWindow.fxml");
        initializeStage(controller, "Password Manager");

    }

    /**
     * Displays the login window to the user
     */
    public void showLoginWindow() {
//        System.out.println(" show login window called");
        BaseController controller = new LoginController();
        controller.setFxmlName("LoginWindow.fxml");
        initializeStage(controller, "My Login");
    }

    /**
     * Displays the new user window to the user
     */
    public void showNewUserWindow() {
//        System.out.println(" show new user window called");
        BaseController controller = new NewUserController();
        controller.setFxmlName("NewUserWindow.fxml");
        initializeStage(controller, "Add New User");
    }

    /**
     * Closes the current stage
     * @param stageToClose
     */
    public void closeStage(Stage stageToClose) {
        stageToClose.close();
    }


}
