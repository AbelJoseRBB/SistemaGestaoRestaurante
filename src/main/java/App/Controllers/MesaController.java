package App.Controllers;

import App.Persistencia.IPersistencia;
import App.Persistencia.PersistenceService;
import Model.Atendimento.Comanda;
import Model.Atendimento.Mesa;
import Model.Produtos.ItemVendavel;
import Model.Produtos.Produto;
import Model.Sistema.Config;
import Model.Usuarios.Interno;
import Model.Usuarios.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MesaController extends BaseController{

    @FXML
    private BorderPane painelRaiz;
    private Node centroOriginalMesas;
    @FXML
    private Label labelUsuario;
    @FXML
    private Button botaoConfig;
    @FXML
    private TilePane painelMesas;
    @FXML
    private Button botaoProdutos;
    @FXML
    private Button botaoUsuarios;

    private Usuario usuarioLogado;
    private List<Mesa> listaDeMesas = new ArrayList<>();
    private List<Produto> listaDeProdutos;
    private List<Usuario> listaDeUsuarios;

    private Config config;
    private IPersistencia persistenceService;

    public void setUsuarioLogado(Usuario usuario, List<Usuario> usuarios, List<Produto> produtos, Config config, IPersistencia service) {
        this.usuarioLogado = usuario;
        this.listaDeUsuarios = usuarios;
        this.listaDeProdutos = produtos;
        this.config = config;
        this.persistenceService = service;

        labelUsuario.setText("Usuário: " + usuario.getNome() +
                " (" + usuario.getClass().getSimpleName() + ")");

        if (usuario instanceof Interno) {
            botaoConfig.setVisible(true);
            botaoProdutos.setVisible(true);
            botaoUsuarios.setVisible(true);
        }

        this.centroOriginalMesas = painelRaiz.getCenter();

        carregarMesas();
    }

    private void carregarMesas() {
        painelMesas.getChildren().clear();
        this.listaDeMesas.clear();

        int numeroTotalDeMesas = this.config.getNumeroDeMesas();
        for (int i = 1; i <= numeroTotalDeMesas; i++) {
            Mesa novaMesa = new Mesa(i);
            this.listaDeMesas.add(novaMesa);
            VBox mesaBox = criarMesaVisual(novaMesa);
            painelMesas.getChildren().add(mesaBox);
        }
    }

    private void atualizarVisualDasMesas() {
        painelMesas.getChildren().clear();
        for (Mesa mesa : this.listaDeMesas) {
            VBox mesaBox = criarMesaVisual(mesa);
            painelMesas.getChildren().add(mesaBox);
        }
    }

    // Em MesaController.java
// SUBSTITUA O SEU MÉTODO INCOMPLETO POR ESTE COMPLETO:

    // Em MesaController.java

    private VBox criarMesaVisual(Mesa mesa) {
        VBox box = new VBox(5);
        String estiloFundo;
        String statusTexto;
        Button botaoAcao = new Button();

        if (mesa.isAguardandoPagamento()) {
            // --- AMARELO: Abre Pagamento Direto ---
            estiloFundo = "-fx-background-color: #fff3cd;";
            statusTexto = "Aguardando Pagamento";

            botaoAcao.setText("Receber / Baixar");
            // AQUI A MUDANÇA: Chama o método de pagamento direto
            botaoAcao.setOnAction(e -> abrirTelaPagamento(mesa));

        } else if (mesa.isOcupada()) {
            // --- VERMELHO ---
            estiloFundo = "-fx-background-color: #f8d7da;";
            statusTexto = "Ocupada (" + mesa.getComandas().size() + ")";
            botaoAcao.setText("Gerenciar");
            botaoAcao.setOnAction(e -> abrirMesaEspecifica(mesa.getNumMesa()));

        } else {
            // --- VERDE ---
            estiloFundo = "-fx-background-color: #d4edda;";
            statusTexto = "Livre";
            botaoAcao.setText("Abrir Mesa");
            botaoAcao.setOnAction(e -> abrirMesaEspecifica(mesa.getNumMesa()));
        }

        box.setStyle("-fx-border-color: #666; -fx-border-radius: 5; -fx-padding: 10; " + estiloFundo);
        box.setPrefSize(100, 80);
        Label label = new Label("Mesa " + mesa.getNumMesa());
        label.setStyle("-fx-font-weight: bold;");
        Label statusLabel = new Label(statusTexto);
        box.getChildren().addAll(label, statusLabel, botaoAcao);
        return box;
    }

    private void abrirMesaEspecifica(int numeroMesa) {
        Mesa mesaSelecionada = this.listaDeMesas.get(numeroMesa - 1);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/GerenciarMesaView.fxml"));
            Parent root = loader.load();
            GerenciarMesaController controller = loader.getController();

            List<ItemVendavel> itensVendaveis = new ArrayList<>(this.persistenceService.carregarProdutos());
            controller.inicializar(mesaSelecionada, this.usuarioLogado, itensVendaveis);

            Stage gerenciarStage = new Stage();
            gerenciarStage.initModality(Modality.APPLICATION_MODAL);
            gerenciarStage.setTitle("Gerenciando Mesa " + numeroMesa);
            gerenciarStage.setScene(new Scene(root));

            // --- MUDANÇA AQUI: REMOVI O MAXIMIZED ---
            // gerenciarStage.setMaximized(true); // <--- APAGUEI ESTA LINHA
            gerenciarStage.setResizable(false); // Opcional: impede esticar a janelinha
            // ----------------------------------------

            gerenciarStage.showAndWait();

            atualizarVisualDasMesas();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o gerenciador da mesa.");
        }
    }

    @FXML
    private void abrirConfiguracoes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/ConfigView.fxml"));
            Node painelConfig = loader.load();
            ConfigController controller = loader.getController();
            controller.inicializar(this.config, this.persistenceService);
            painelRaiz.setCenter(painelConfig);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível carregar a tela de configurações.");
        }
    }

    @FXML
    private void abrirDashboardMesas() {
        sincronizarMesasComConfig();
        painelRaiz.setCenter(this.centroOriginalMesas);
    }

    @FXML
    private void abrirProdutos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/ProdutosView.fxml"));
            Node painelProdutos = loader.load();
            ProdutosController controller = loader.getController();
            this.listaDeProdutos = persistenceService.carregarProdutos();
            controller.inicializar(this.listaDeProdutos, this.persistenceService);
            painelRaiz.setCenter(painelProdutos);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível carregar a tela de produtos.");
        }
    }

    @FXML
    private void abrirUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/UsuariosView.fxml"));
            Node painelUsuarios = loader.load();
            UsuariosController controller = loader.getController();

            controller.inicializar(this.listaDeUsuarios, (PersistenceService) this.persistenceService);

            painelRaiz.setCenter(painelUsuarios);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível carregar a tela de usuários.");
        }
    }
    private void sincronizarMesasComConfig() {
        int numeroAtualNaLista = this.listaDeMesas.size();
        int numeroDesejadoDoConfig = this.config.getNumeroDeMesas();

        if (numeroAtualNaLista == numeroDesejadoDoConfig) {
            atualizarVisualDasMesas();
            return;
        }

        if (numeroDesejadoDoConfig > numeroAtualNaLista) {
            for (int i = numeroAtualNaLista + 1; i <= numeroDesejadoDoConfig; i++) {
                Mesa novaMesa = new Mesa(i);
                this.listaDeMesas.add(novaMesa);
            }
        } else {
            this.listaDeMesas.removeIf(mesa -> mesa.getNumMesa() > numeroDesejadoDoConfig);
        }
        atualizarVisualDasMesas();
    }

    private void abrirTelaPagamento(Mesa mesaParaPagar) {
        try {
            double total = 0.0;
            for (Comanda c : mesaParaPagar.getComandas()) {
                total += c.calcularTotal();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/PagamentoView.fxml"));
            Parent root = loader.load();

            PagamentoController pgtoController = loader.getController();
            pgtoController.inicializar(total, mesaParaPagar);

            Stage stage = new Stage();
            stage.setTitle("Recebendo Mesa " + mesaParaPagar.getNumMesa());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            // --- REMOVA A LINHA DE MAXIMIZAR DAQUI ---
            // stage.setMaximized(true); <--- APAGUE ISSO
            // -----------------------------------------

            // Dica: Se quiser impedir que o usuário estique a janela de pagamento:
            stage.setResizable(false);

            stage.showAndWait();

            atualizarVisualDasMesas();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o pagamento.");
        }
    }
}