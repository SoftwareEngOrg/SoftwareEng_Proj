module com.example.softwareengproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    exports Presentation;
    opens Presentation to javafx.fxml;
}