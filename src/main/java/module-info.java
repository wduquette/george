module com.wjduquette.george {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.wjduquette.george to javafx.fxml;
    exports com.wjduquette.george;
}