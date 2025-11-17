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

import javafx.stage.Stage;

public class GerenciarMesaController extends BaseController{

    @FXML
    private Label labelTituloMesa;
    @FXML
    private ListView<Comanda> listaComandas;
    @FXML
    private Button botaoAbrirComanda;
    @FXML
    private Button botaoReabrirComanda;

    @FXML
    private Button botaoFecharMesa;

    @FXML
    private Button botaoAdicionarComanda;

    @FXML
    private Button botaoReabrirMesa;

    @FXML
    private Button botaoReabrirMesaInteira;

    private Mesa mesa;
    private Usuario atendente;
    private List<ItemVendavel> produtosDisponiveis;


    // Em GerenciarMesaController.java

    public void inicializar(Mesa mesa, Usuario atendente, List<ItemVendavel> itens) {
        this.mesa = mesa;
        this.atendente = atendente;
        this.produtosDisponiveis = itens;
        labelTituloMesa.setText("Gerenciando Mesa " + mesa.getNumMesa());
        atualizarListaComandas();

        // --- ESTA É A LÓGICA PRINCIPAL ---

        boolean mesaFechada = this.mesa.isAguardandoPagamento();

        // 1. Botão "Adicionar Comanda"
        //    (Desabilita se a mesa estiver fechada)
        botaoAdicionarComanda.setDisable(mesaFechada);

        // 2. Botão "Fechar Conta da Mesa"
        //    (Mostra se a mesa NÃO estiver fechada E tiver comandas)
        botaoFecharMesa.setVisible(!mesaFechada && !this.mesa.getComandas().isEmpty());

        // 3. Botão "Reabrir Mesa" (com o nome novo)
        //    (Mostra APENAS se a mesa estiver fechada)
        botaoReabrirMesaInteira.setVisible(mesaFechada);

        // --- FIM DA LÓGICA ---

        // Listener para os botões "Abrir" e "Reabrir" (individuais)
        this.listaComandas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> atualizarVisibilidadeBotoes(newValue)
        );
        atualizarVisibilidadeBotoes(null);
    }

    private void atualizarVisibilidadeBotoes(Comanda selecionada) {

        // --- REGRA MESTRE ADICIONADA ---
        // Se a mesa inteira está fechada, NENHUM botão individual
        // de comanda pode aparecer.
        if (this.mesa.isAguardandoPagamento()) {
            botaoAbrirComanda.setVisible(false);
            botaoReabrirComanda.setVisible(false);
            return; // Para a execução aqui
        }
        // --- FIM DA REGRA MESTRE ---

        // Se a mesa NÃO está fechada, execute a lógica antiga:
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

    // Em GerenciarMesaController.java

    @FXML
    private void reabrirComandaSelecionada() {
        Comanda selecionada = listaComandas.getSelectionModel().getSelectedItem();

        if (selecionada != null && selecionada.isFechada()) {
            selecionada.reabrir(); // Reabre a comanda individual

            // --- ADICIONE ESTA LINHA ---
            // Pede para a Mesa "se checar"
            this.mesa.verificarStatusParaPagamento();
            // --- FIM DA LINHA ---

            // Atualiza a tela inteira (isso é importante)
            atualizarListaComandas(); // Remove o "(FECHADA)"
            atualizarVisibilidadeBotoes(selecionada); // Mostra/esconde botões individuais

            // Atualiza os botões da MESA (Fechar/Reabrir)
            boolean mesaFechada = this.mesa.isAguardandoPagamento();
            botaoAdicionarComanda.setDisable(mesaFechada);
            botaoFecharMesa.setVisible(!mesaFechada && !this.mesa.getComandas().isEmpty());
            botaoReabrirMesaInteira.setVisible(mesaFechada);

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
    // Dentro de GerenciarMesaController.java

    // Em GerenciarMesaController.java

    @FXML
    private void fecharMesaParaPagamento() {

        // 1. Fecha cada comanda individualmente
        for (Comanda comanda : this.mesa.getComandas()) {
            if (!comanda.isFechada()) {
                comanda.fechar();
            }
        }

        // 2. Marca a mesa para pagamento (deixa amarela)
        this.mesa.setAguardandoPagamento(true);

        // 3. Desabilita o botão de adicionar
        botaoAdicionarComanda.setDisable(true);

        // --- ESTA É A LINHA QUE FALTAVA ---
        // 4. Force a lista visual a se redesenhar AGORA
        //    Isso vai fazer os "(FECHADA)" aparecerem antes da janela fechar.
        atualizarListaComandas();
        // --- FIM DA LINHA ADICIONADA ---

        // 5. Fecha esta janela (GerenciarMesaView)
        Stage stage = (Stage) botaoFecharMesa.getScene().getWindow();
        stage.close();
    }

    // Cole este método dentro da sua classe GerenciarMesaController

    @FXML
    private void reabrirMesaInteira() {
        // 1. Reverte o status da mesa
        this.mesa.setAguardandoPagamento(false);

        // 2. Reabilita o botão de adicionar comanda
        botaoAdicionarComanda.setDisable(false);

        // 3. Fecha a janela
        Stage stage = (Stage) botaoReabrirMesa.getScene().getWindow();
        stage.close();
    }
}