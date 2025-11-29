package App.Controllers;

import Model.Atendimento.Comanda;
import Model.Atendimento.Mesa;
import Model.Produtos.ItemVendavel;
import Model.Usuarios.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GerenciarMesaController extends BaseController {

    @FXML private Label labelTituloMesa;
    @FXML private ListView<Comanda> listaComandas;

    @FXML private Button botaoAbrirComanda;
    @FXML private Button botaoReabrirComanda;

    @FXML private Button botaoAdicionarComanda;
    @FXML private Button botaoFecharMesa;
    @FXML private Button botaoReabrirMesaInteira;
    // Removido: botaoRegistrarPagamento

    private Mesa mesa;
    private Usuario atendente;
    private List<ItemVendavel> produtosDisponiveis;

    public void inicializar(Mesa mesa, Usuario atendente, List<ItemVendavel> itens) {
        this.mesa = mesa;
        this.atendente = atendente;
        this.produtosDisponiveis = itens;
        labelTituloMesa.setText("Gerenciando Mesa " + mesa.getNumMesa());
        atualizarListaComandas();

        boolean mesaFechada = this.mesa.isAguardandoPagamento();

        botaoAdicionarComanda.setDisable(mesaFechada);
        botaoFecharMesa.setVisible(!mesaFechada && !this.mesa.getComandas().isEmpty());

        // Apenas Reabrir (Pagar agora é na tela principal)
        botaoReabrirMesaInteira.setVisible(mesaFechada);

        this.listaComandas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> atualizarVisibilidadeBotoes(newValue)
        );
        atualizarVisibilidadeBotoes(null);
    }

    private void atualizarVisibilidadeBotoes(Comanda selecionada) {
        if (this.mesa.isAguardandoPagamento()) {
            botaoAbrirComanda.setVisible(false);
            botaoReabrirComanda.setVisible(false);
            return;
        }

        if (selecionada == null) {
            botaoAbrirComanda.setVisible(false);
            botaoReabrirComanda.setVisible(false);
        } else if (selecionada.isFechada()) {
            botaoAbrirComanda.setVisible(false);
            botaoReabrirComanda.setVisible(true);
        } else {
            botaoAbrirComanda.setVisible(true);
            botaoReabrirComanda.setVisible(false);
        }
    }

    private void atualizarListaComandas() {
        listaComandas.getItems().clear();
        listaComandas.getItems().addAll(mesa.getComandas());
    }

    @FXML
    private void adicionarNovaComanda() {
        if (this.mesa.isAguardandoPagamento()) {
            mostrarAlerta("Mesa Fechada", "Não é possível adicionar comanda em mesa aguardando pagamento.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nova Comanda");
        dialog.setHeaderText("Mesa " + mesa.getNumMesa());
        dialog.setContentText("Nome do cliente:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()){
            Comanda novaComanda = new Comanda();
            novaComanda.setClienteNome(result.get());
            this.mesa.adicionarComanda(novaComanda);

            atualizarListaComandas();
            botaoFecharMesa.setVisible(true);

            abrirJanelaDaComanda(novaComanda);
        }
    }

    @FXML
    private void fecharMesaParaPagamento() {
        for (Comanda comanda : this.mesa.getComandas()) {
            if (!comanda.isFechada()) comanda.fechar();
        }
        this.mesa.setAguardandoPagamento(true);
        atualizarListaComandas();
        botaoAdicionarComanda.setDisable(true);
        ((Stage) botaoFecharMesa.getScene().getWindow()).close();
    }

    @FXML
    private void reabrirMesaInteira() {
        this.mesa.setAguardandoPagamento(false);
        botaoAdicionarComanda.setDisable(false);
        ((Stage) botaoReabrirMesaInteira.getScene().getWindow()).close();
    }

    @FXML
    private void abrirComandaSelecionada() {
        Comanda selecionada = listaComandas.getSelectionModel().getSelectedItem();
        if (selecionada != null) abrirJanelaDaComanda(selecionada);
    }

    @FXML
    private void reabrirComandaSelecionada() {
        Comanda selecionada = listaComandas.getSelectionModel().getSelectedItem();
        if (selecionada != null && selecionada.isFechada()) {
            selecionada.reabrir();
            this.mesa.verificarStatusParaPagamento();
            atualizarListaComandas();
            inicializar(this.mesa, this.atendente, this.produtosDisponiveis);
            mostrarAlerta("Sucesso", "Comanda reaberta.");
        }
    }

    private void abrirJanelaDaComanda(Comanda comandaParaAbrir) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/ComandaView.fxml"));
            Parent root = loader.load();
            ComandaController comandaController = loader.getController();
            comandaController.carregarComanda(this.mesa, comandaParaAbrir, this.atendente, this.produtosDisponiveis);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editando " + comandaParaAbrir.toString());
            stage.setScene(new Scene(root));

            // --- MUDANÇA AQUI: ADICIONEI O MAXIMIZED ---
            stage.setMaximized(true); // <--- TELA CHEIA PARA ADICIONAR PRODUTOS
            // -------------------------------------------

            stage.showAndWait();

            atualizarListaComandas();

            // Verifica status da mesa ao voltar
            this.mesa.verificarStatusParaPagamento();
            boolean mesaFechada = this.mesa.isAguardandoPagamento();
            botaoAdicionarComanda.setDisable(mesaFechada);
            botaoFecharMesa.setVisible(!mesaFechada && !this.mesa.getComandas().isEmpty());
            botaoReabrirMesaInteira.setVisible(mesaFechada);


        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir a tela da comanda.");
        }
    }

    @Override
    protected void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}