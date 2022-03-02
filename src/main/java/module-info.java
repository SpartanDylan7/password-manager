module org.eagle.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires org.apache.derby.engine;
    requires org.apache.derby.commons;

    opens org.eagle.passwordmanager.controller to javafx.fxml;



    exports org.eagle.passwordmanager;
    exports org.eagle.passwordmanager.controller to javafx.fxml;
}