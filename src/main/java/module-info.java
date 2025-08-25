module com.example.map_toysocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens com.example.map_toysocialnetwork to javafx.fxml;
    exports com.example.map_toysocialnetwork;
}

