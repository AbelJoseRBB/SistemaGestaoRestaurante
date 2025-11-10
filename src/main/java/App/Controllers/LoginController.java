package App.Controllers;

import App.Persistencia.IPersistencia;
import App.Persistencia.PersistenceService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import Model.Usuarios.*;
import Model.Produtos.Produto;
import Model.Sistema.Config;

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

    private IPersistencia persistenceService;
    private Config config;

    private List<Usuario> listaDeUsuarios = new ArrayList<>();
    private List<Produto> listaDeProdutos = new ArrayList<>();

    @FXML
    public void initialize() {
        this.persistenceService = new PersistenceService();
        this.config = persistenceService.carregarConfig();
        carregarUsuarios();
        carregarProdutos();
    }

    // --- MUDANÇA: Lê do PersistenceService ---
    private void carregarUsuarios() {
        this.listaDeUsuarios = persistenceService.carregarUsuarios();
    }

    private void carregarProdutos() {
        this.listaDeProdutos.clear();
        this.listaDeProdutos = persistenceService.carregarProdutos();
    }

    @FXML
    private void fazerLogin(ActionEvent event) {
        String nome = campoUsuario.getText().trim();
        String senha = campoSenha.getText();
        Usuario usuarioEncontrado = null;

        for (Usuario u : this.listaDeUsuarios) {
            if (u.autenticar(nome, senha)) {
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

            mesaController.setUsuarioLogado(
                    usuario,
                    this.listaDeUsuarios,
                    this.listaDeProdutos,
                    this.config,
                    this.persistenceService
            );

            Stage stage = (Stage) campoUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Mesas - Sistema Restaurante");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}