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


    private ViewFactory() {

    }

    public static ViewFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewFactory();
        }
        return INSTANCE;
    }

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

    public void showMainWindow() {
//        System.out.println(" show main window called");
        BaseController controller = new MainController();
        controller.setFxmlName("MainWindow.fxml");
        initializeStage(controller, "Password Manager");

    }

    public void showLoginWindow() {
//        System.out.println(" show login window called");
        BaseController controller = new LoginController();
        controller.setFxmlName("LoginWindow.fxml");
        initializeStage(controller, "My Login");
    }

    public void showNewUserWindow() {
//        System.out.println(" show new user window called");
        BaseController controller = new NewUserController();
        controller.setFxmlName("NewUserWindow.fxml");
        initializeStage(controller, "Add New User");
    }


    public void closeStage(Stage stageToClose) {
        stageToClose.close();
    }


}
