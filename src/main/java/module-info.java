module ru.guu.dz_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens ru.guu.dz_2 to javafx.fxml;
    exports ru.guu.dz_2;
}