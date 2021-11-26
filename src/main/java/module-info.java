module com.wjduquette.george {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.wjduquette.george to javafx.fxml;
    exports com.wjduquette.george;

    opens com.wjduquette.george.tmx to com.google.gson;
    exports com.wjduquette.george.tmx;
}
