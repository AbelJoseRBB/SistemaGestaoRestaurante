package App.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import Model.Usuarios.*;

public class MesaController {

    @FXML
    private Label labelUsuario;

    @FXML
    private Button botaoConfig;

    @FXML
    private TilePane painelMesas;

    private Usuario usuarioLogado;

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        labelUsuario.setText("Usuário: " + usuario.getNome() +
                " (" + usuario.getClass().getSimpleName() + ")");

        // Apenas o Interno pode ver o botão de configurações
        if (usuario instanceof Interno) {
            botaoConfig.setVisible(true);
        }

        carregarMesas();
    }

    private void carregarMesas() {
        painelMesas.getChildren().clear();

        // Exemplo de 10 mesas
        for (int i = 1; i <= 10; i++) {
            VBox mesaBox = criarMesaVisual(i);
            painelMesas.getChildren().add(mesaBox);
        }
    }

    private VBox criarMesaVisual(int numeroMesa) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #666; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #eaeaea;");
        box.setPrefSize(100, 80);

        Label label = new Label("Mesa " + numeroMesa);
        label.setStyle("-fx-font-weight: bold;");

        Button botaoAbrir = new Button("Abrir");
        botaoAbrir.setOnAction(e -> abrirComandas(numeroMesa));

        box.getChildren().addAll(label, botaoAbrir);
        return box;
    }

    private void abrirComandas(int numeroMesa) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Abrir Mesa");
        alerta.setHeaderText(null);
        alerta.setContentText("Abrindo comandas da mesa " + numeroMesa + "...");
        alerta.showAndWait();
        // Aqui futuramente abriremos a tela de comandas
    }

    @FXML
    private void abrirConfiguracoes() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Configurações");
        alerta.setHeaderText(null);
        alerta.setContentText("Acesso às configurações do sistema (somente interno).");
        alerta.showAndWait();
    }
}