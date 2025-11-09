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

    opens Model.Usuarios to com.google.gson;
    opens Model.Produtos to com.google.gson;
    exports App.Persistencia;
    opens App.Persistencia to javafx.fxml;
    exports Model.Atendimento;
    opens Model.Atendimento to com.google.gson;
    exports Model.Sistema;
    opens Model.Sistema to com.google.gson;
}