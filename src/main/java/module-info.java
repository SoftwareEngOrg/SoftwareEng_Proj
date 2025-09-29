module com.example.softwareengproj {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.softwareengproj to javafx.fxml;
    exports com.example.softwareengproj;
}