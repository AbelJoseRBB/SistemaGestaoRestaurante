package App.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Model.Usuarios.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoSenha;

    @FXML
    private Label mensagemErro;

    private final List<Usuario> usuarios = new ArrayList<>();

    @FXML
    public void initialize() {
        // Usuários de exemplo (depois pode vir de um arquivo ou BD)
        usuarios.add(new Garcom("joao", "123"));
        usuarios.add(new Interno("admin", "admin"));
    }

    @FXML
    private void fazerLogin(ActionEvent event) {
        String nome = campoUsuario.getText();
        String senha = campoSenha.getText();

        Usuario usuarioEncontrado = null;

        for (Usuario u : usuarios) {
            if (u.getNome().equals(nome) && u.getSenha().equals(senha)) {
                usuarioEncontrado = u;
                break;
            }
        }

        if (usuarioEncontrado != null) {
            abrirTelaMesas(usuarioEncontrado);
        } else {
            mensagemErro.setText("Usuário ou senha incorretos!");
        }
    }

    private void abrirTelaMesas(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Mesa.fxml"));
            Parent root = loader.load();

            MesaController mesaController = loader.getController();
            mesaController.setUsuarioLogado(usuario);

            Stage stage = (Stage) campoUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mesas - Sistema Restaurante");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}