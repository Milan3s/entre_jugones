module app.entre_jugones {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens app.entre_jugones to javafx.fxml;
    exports app.entre_jugones;
}
