package App.Controllers;

import Model.Atendimento.Comanda;
import Model.Atendimento.Mesa;
import Model.Produtos.ItemVendavel;
import Model.Usuarios.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GerenciarMesaController extends BaseController{

    @FXML
    private Label labelTituloMesa;
    @FXML
    private ListView<Comanda> listaComandas;
    @FXML
    private Button botaoAbrirComanda;
    @FXML
    private Button botaoReabrirComanda;

    private Mesa mesa;
    private Usuario atendente;
    private List<ItemVendavel> produtosDisponiveis;

    public void inicializar(Mesa mesa, Usuario atendente, List<ItemVendavel> produtos) {
        this.mesa = mesa;
        this.atendente = atendente;
        this.produtosDisponiveis = produtos;
        labelTituloMesa.setText("Gerenciando Mesa " + mesa.getNumMesa());
        atualizarListaComandas();

        this.listaComandas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> atualizarVisibilidadeBotoes(newValue)
        );
        atualizarVisibilidadeBotoes(null);
    }

    private void atualizarVisibilidadeBotoes(Comanda selecionada) {
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

    @FXML
    private void reabrirComandaSelecionada() {
        Comanda selecionada = listaComandas.getSelectionModel().getSelectedItem();

        if (selecionada != null && selecionada.isFechada()) {
            selecionada.reabrir();

            atualizarListaComandas();
            atualizarVisibilidadeBotoes(selecionada);

            mostrarAlerta("Sucesso", "A comanda #" + selecionada.getId() + " foi reaberta.");
        }
    }

    private void atualizarListaComandas() {
        listaComandas.getItems().clear();
        listaComandas.getItems().addAll(mesa.getComandas());
    }

    @FXML
    private void adicionarNovaComanda() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nova Comanda");
        dialog.setHeaderText("Adicionando nova comanda para a Mesa " + mesa.getNumMesa());
        dialog.setContentText("Digite o nome do cliente:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()){
            String nomeCliente = result.get();

            Comanda novaComanda = new Comanda();
            novaComanda.setClienteNome(nomeCliente);
            this.mesa.adicionarComanda(novaComanda);

            atualizarListaComandas();

            abrirJanelaDaComanda(novaComanda);

        }
    }

    @FXML
    private void abrirComandaSelecionada() {
        Comanda selecionada = listaComandas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Erro", "Nenhuma comanda foi selecionada.");
            return;
        }
        abrirJanelaDaComanda(selecionada);
    }

    private void abrirJanelaDaComanda(Comanda comandaParaAbrir) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/ComandaView.fxml"));
            Parent root = loader.load();
            ComandaController comandaController = loader.getController();
            comandaController.carregarComanda(this.mesa, comandaParaAbrir, this.atendente, this.produtosDisponiveis);
            Stage stageAtual = (Stage) listaComandas.getScene().getWindow();
            stageAtual.setScene(new Scene(root));
            stageAtual.setMaximized(true);
            stageAtual.setTitle("Editando " + comandaParaAbrir.toString());

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir a tela da comanda.");
        }
    }
}