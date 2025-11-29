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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GerenciarMesaController extends BaseController {

    @FXML private Label labelTituloMesa;
    @FXML private ListView<Comanda> listaComandas;

    @FXML private Button botaoAbrirComanda;
    @FXML private HBox boxBotoesFechada;

    @FXML private Button botaoAdicionarComanda;
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

        // Listener para mudar os botões
        this.listaComandas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> atualizarVisibilidadeBotoes(newValue)
        );
        atualizarVisibilidadeBotoes(null);
    }

    private void atualizarVisibilidadeBotoes(Comanda selecionada) {
        if (selecionada == null) {
            botaoAbrirComanda.setVisible(false);
            boxBotoesFechada.setVisible(false);
        } else if (selecionada.isFechada()) {
            botaoAbrirComanda.setVisible(false);
            boxBotoesFechada.setVisible(true); // Mostra Reabrir e Pagar
        } else {
            botaoAbrirComanda.setVisible(true); // Mostra Abrir
            boxBotoesFechada.setVisible(false);
        }
    }

    private void atualizarListaComandas() {
        listaComandas.getItems().clear();
        listaComandas.getItems().addAll(mesa.getComandas());
    }

    @FXML
    private void pagarComandaSelecionada() {
        Comanda selecionada = listaComandas.getSelectionModel().getSelectedItem();
        if (selecionada == null || !selecionada.isFechada()) {
            mostrarAlerta("Erro", "Selecione uma comanda FECHADA para pagar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/PagamentoView.fxml"));
            Parent root = loader.load();

            PagamentoController pgtoController = loader.getController();
            // Passa APENAS a comanda selecionada
            pgtoController.inicializar(selecionada);

            Stage stage = new Stage();
            stage.setTitle("Pagamento - Comanda " + selecionada.getClienteNome());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            // Se o pagamento foi confirmado
            if (pgtoController.isPagamentoRealizado()) {
                // Remove a comanda da mesa (já foi paga)
                this.mesa.removerComanda(selecionada); // Certifique-se que Mesa tem esse método

                // Atualiza a tela
                atualizarListaComandas();
                atualizarVisibilidadeBotoes(null);

                // Se não sobrar nenhuma comanda, a mesa fica livre automaticamente
                // (assumindo que Mesa.isOcupada() verifica se a lista está vazia)
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o pagamento.");
        }
    }

    @FXML
    private void adicionarNovaComanda() {
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
            abrirJanelaDaComanda(novaComanda);
        }
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
            atualizarListaComandas();
            // Atualiza os botões (vai esconder o Pagar e mostrar o Abrir)
            atualizarVisibilidadeBotoes(selecionada);
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
            stage.setMaximized(true);
            stage.showAndWait();

            atualizarListaComandas();

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