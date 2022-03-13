package org.eagle.passwordmanager;

import javafx.application.Application;
import javafx.stage.Stage;
import org.eagle.passwordmanager.util.DatabaseManager;


import java.sql.SQLException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws SQLException {

        DatabaseManager.initializeDatabase();


        ViewFactory.getInstance().showLoginWindow();
    }
//Code Begins Here
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

    }
}
