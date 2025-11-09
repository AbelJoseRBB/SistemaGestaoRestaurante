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
import javafx.scene.control.TextInputDialog; // <-- 1. IMPORTA O POP-UP DE TEXTO
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional; // <-- 2. IMPORTA O "Optional" (para o pop-up)

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
            // Nenhuma comanda selecionada
            botaoAbrirComanda.setVisible(false);
            botaoReabrirComanda.setVisible(false);
        } else if (selecionada.isFechada()) {
            // Comanda FECHADA selecionada
            botaoAbrirComanda.setVisible(false);
            botaoReabrirComanda.setVisible(true); // Mostra "Reabrir"
        } else {
            // Comanda ABERTA selecionada
            botaoAbrirComanda.setVisible(true); // Mostra "Abrir"
            botaoReabrirComanda.setVisible(false);
        }
    }

    @FXML
    private void reabrirComandaSelecionada() {
        Comanda selecionada = listaComandas.getSelectionModel().getSelectedItem();

        if (selecionada != null && selecionada.isFechada()) {
            selecionada.reabrir(); // Lógica de reabrir

            // Atualiza a UI
            atualizarListaComandas(); // O texto "(FECHADA)" vai sumir
            atualizarVisibilidadeBotoes(selecionada); // Os botões vão trocar

            mostrarAlerta("Sucesso", "A comanda #" + selecionada.getId() + " foi reaberta.");
        }
    }

    private void atualizarListaComandas() {
        listaComandas.getItems().clear();
        listaComandas.getItems().addAll(mesa.getComandas());
    }

    // --- A GRANDE MUDANÇA ESTÁ AQUI ---
    @FXML
    private void adicionarNovaComanda() {

        // 1. Pergunta o nome do cliente PRIMEIRO
        TextInputDialog dialog = new TextInputDialog(); // Pode botar um nome padrão ex: "Cliente"
        dialog.setTitle("Nova Comanda");
        dialog.setHeaderText("Adicionando nova comanda para a Mesa " + mesa.getNumMesa());
        dialog.setContentText("Por favor, digite o nome do cliente:");

        // 2. Mostra o pop-up e espera o usuário digitar
        Optional<String> result = dialog.showAndWait();

        // 3. Verifica se o usuário digitou um nome e clicou OK
        if (result.isPresent() && !result.get().trim().isEmpty()){
            String nomeCliente = result.get(); // Pega o nome

            // 4. Cria o objeto Comanda
            Comanda novaComanda = new Comanda();

            // 5. DEFINE O NOME DO CLIENTE IMEDIATAMENTE
            novaComanda.setClienteNome(nomeCliente);

            // 6. Adiciona ele na Mesa
            this.mesa.adicionarComanda(novaComanda);

            // 7. Atualiza a lista visual (agora vai mostrar o nome certo no "toString()")
            atualizarListaComandas();

            // 8. Abre a tela de edição para essa comanda nova
            // (Ela já vai abrir com o nome do cliente preenchido)
            abrirJanelaDaComanda(novaComanda);

        } else {
            // Usuário cancelou ou não digitou nada
            // Não fazemos nada.
        }
    }
    // --- FIM DA MUDANÇA ---

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
            stageAtual.setTitle("Editando " + comandaParaAbrir.toString());

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir a tela da comanda.");
        }
    }
}