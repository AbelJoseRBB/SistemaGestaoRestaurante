package App.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import Model.Usuarios.*;
import Model.Produtos.Produto;
import Model.Config;

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

    private PersistenceService persistenceService;
    private Config config;

    private List<Usuario> listaDeUsuarios = new ArrayList<>();
    private List<Produto> listaDeProdutos = new ArrayList<>();

    @FXML
    public void initialize() {
        this.persistenceService = new PersistenceService();
        this.config = persistenceService.carregarConfig();

        // --- MUDANÇA: Agora carrega a lista REAL (do arquivo) ---
        carregarUsuarios();
        // --- FIM DA MUDANÇA ---

        carregarProdutos(); // (Sem mudanças)
    }

    // --- MUDANÇA: Lê do PersistenceService ---
    private void carregarUsuarios() {
        // Pede para o PersistenceService carregar e retorna a lista
        this.listaDeUsuarios = persistenceService.carregarUsuarios();
    }
    // --- FIM DA MUDANÇA ---

    private void carregarProdutos() {
        this.listaDeProdutos.clear();
        this.listaDeProdutos.add(new Produto("Cerveja", "Lata 350ml", 5.00, 100));
        this.listaDeProdutos.add(new Produto("Agua", "Garrafa 500ml", 3.00, 100));
        this.listaDeProdutos.add(new Produto("Petisco", "Porção de Batata", 25.00, 50));
        this.listaDeProdutos.add(new Produto("Refrigerante", "Lata 350ml", 4.50, 80));
    }

    @FXML
    private void fazerLogin(ActionEvent event) {
        String nome = campoUsuario.getText();
        String senha = campoSenha.getText();
        Usuario usuarioEncontrado = null;

        for (Usuario u : this.listaDeUsuarios) {
            // NOTE: A gente usa o método autenticar que você criou!
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

            // Passa as listas e serviços para o dashboard
            mesaController.setUsuarioLogado(
                    usuario,
                    this.listaDeUsuarios, // Lista REAL
                    this.listaDeProdutos,
                    this.config,
                    this.persistenceService
            );

            Stage stage = (Stage) campoUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mesas - Sistema Restaurante");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}