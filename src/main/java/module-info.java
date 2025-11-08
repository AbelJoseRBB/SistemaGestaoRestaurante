module restaurante.sistemagestaorestaurante {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.google.gson;
    requires annotations;

    opens App to javafx.fxml;
    exports App;

    exports App.Controllers;
    opens App.Controllers to javafx.fxml;

    exports Model;
    opens Model to com.google.gson;
    opens Model.Usuarios to com.google.gson;
    opens Model.Produtos to com.google.gson;
}