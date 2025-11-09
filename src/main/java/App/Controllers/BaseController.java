package App.Controllers;

import javafx.scene.control.Alert;

public abstract class BaseController {

    protected void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
