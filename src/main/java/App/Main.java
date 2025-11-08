package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class Main extends Application {
    @Override
    public void start(@NotNull Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/App/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login - Sistema Restaurante");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}