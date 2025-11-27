module com.example.softwareengproj {
    requires javafx.controls;
    requires javafx.fxml;



    exports Presentation;
    opens Presentation to javafx.fxml;
}