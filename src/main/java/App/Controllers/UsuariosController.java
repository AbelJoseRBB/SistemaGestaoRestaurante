package App.Controllers;

import App.Persistencia.IPersistencia;
import Model.Usuarios.Garcom;
import Model.Usuarios.Interno;
import Model.Usuarios.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.List;

public class UsuariosController extends BaseController{

    @FXML
    private ListView<Usuario> listaUsuarios;
    @FXML
    private Label labelFormulario;
    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoSenha;
    @FXML
    private RadioButton radioGarcom;
    @FXML
    private RadioButton radioInterno;
    @FXML
    private ToggleGroup grupoTipo;

    private List<Usuario> listaUsuariosCentral;
    private ObservableList<Usuario> observableListUsuarios;
    private IPersistencia persistenceService; // <-- NOVO: Para salvar
    private Usuario usuarioSelecionado = null;

    // Inicializa com a lista e o service
    public void inicializar(List<Usuario> listaUsuariosCentral, IPersistencia persistenceService) {
        this.listaUsuariosCentral = listaUsuariosCentral;
        this.persistenceService = persistenceService; // Guarda o service

        this.observableListUsuarios = FXCollections.observableArrayList(listaUsuariosCentral);
        this.listaUsuarios.setItems(observableListUsuarios);

        this.listaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selecionarUsuario(newValue);
                }
        );

        limparCampos();
    }

    private void selecionarUsuario(Usuario usuario) {
        this.usuarioSelecionado = usuario;

        if (usuario == null) {
            limparCampos();
            return;
        }

        labelFormulario.setText("Editando Usuário: " + usuario.getNome());
        campoNome.setText(usuario.getNome());
        campoSenha.setText(usuario.getSenha());

        // Usa a propriedade que você criou para setar o RadioButton
        if (!usuario.hasAcessoEstoque()) { // Garcom: acessoConfig = false
            radioGarcom.setSelected(true);
        } else { // Interno: acessoConfig = true
            radioInterno.setSelected(true);
        }
    }

    @FXML
    private void salvar() {
        try {
            String nome = campoNome.getText();
            String senha = campoSenha.getText();
            if (nome == null || nome.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
                mostrarAlerta("Erro", "Nome e Senha são obrigatórios.");
                return;
            }

            boolean ehGarcom = radioGarcom.isSelected();
            boolean ehInterno = radioInterno.isSelected();

            if (!ehGarcom && !ehInterno) {
                mostrarAlerta("Erro", "Você precisa selecionar um tipo (Garçom ou Interno).");
                return;
            }

            if (this.usuarioSelecionado == null) {
                // --- NOVO USUÁRIO ---
                Usuario novoUsuario;
                if (ehGarcom) {
                    novoUsuario = new Garcom(nome, senha);
                } else {
                    novoUsuario = new Interno(nome, senha);
                }

                this.listaUsuariosCentral.add(novoUsuario);
                this.observableListUsuarios.add(novoUsuario);

            } else {
                // --- EDIÇÃO DE USUÁRIO EXISTENTE ---
                this.usuarioSelecionado.setNome(nome);
                this.usuarioSelecionado.setSenha(senha);
                this.listaUsuarios.refresh();
            }

            // --- A MÁGICA: SALVA NO HD APÓS CADA MUDANÇA ---
            persistenceService.salvarUsuarios(listaUsuariosCentral);

            limparCampos();

        } catch (Exception e) {
            mostrarAlerta("Erro", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void removerUsuario() {
        if (this.usuarioSelecionado == null) {
            mostrarAlerta("Erro", "Nenhum usuário selecionado para remover.");
            return;
        }

        if (this.usuarioSelecionado.getNome().equals("admin")) {
            mostrarAlerta("Acesso Negado", "Você não pode remover o usuário 'admin' principal.");
            return;
        }

        this.listaUsuariosCentral.remove(this.usuarioSelecionado);
        this.observableListUsuarios.remove(this.usuarioSelecionado);

        // --- A MÁGICA: SALVA NO HD APÓS CADA MUDANÇA ---
        persistenceService.salvarUsuarios(listaUsuariosCentral);

        limparCampos();
    }

    @FXML
    private void limparCampos() {
        this.usuarioSelecionado = null;
        this.listaUsuarios.getSelectionModel().clearSelection();

        labelFormulario.setText("Adicionar Novo Usuário");
        campoNome.clear();
        campoSenha.clear();
        grupoTipo.selectToggle(null);
    }

}