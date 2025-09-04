module org.example.javafxui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires core;

    opens org.example.javafxui to javafx.fxml;
    exports org.example.javafxui;
}