package App.Controllers;

import Model.Comanda;
import Model.Mesa;
import Model.Pedido;
import Model.Produtos.Produto;
import Model.Usuarios.Usuario;
// --- MUDANÇA: Imports novos ---
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
// --- FIM ---
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComandaController {

    @FXML
    private Label labelTituloComanda;
    @FXML
    private TextField campoCliente;
    @FXML
    private ListView<Pedido> listaPedidos; // A lista visual
    @FXML
    private Label labelTotal;
    // ... (outros botões)

    private Mesa mesa;
    private Comanda comanda;
    private Usuario atendente;
    private List<Produto> produtosDisponiveis;

    // --- MUDANÇA: Lista Observável ---
    // Esta lista vai "espelhar" a lista de pedidos da comanda
    private ObservableList<Pedido> observableListPedidos;
    // --- FIM ---

    public void carregarComanda(Mesa mesa, Comanda comanda, Usuario atendente, List<Produto> produtos) {
        this.mesa = mesa;
        this.comanda = comanda;
        this.atendente = atendente;
        this.produtosDisponiveis = produtos;

        labelTituloComanda.setText("Editando " + comanda.toString());
        campoCliente.setText(comanda.getClienteNome());

        // --- MUDANÇA: Configura a Lista Observável ---
        // 1. Pega a lista ORIGINAL da comanda (que não é mais cópia)
        List<Pedido> pedidosDaComanda = this.comanda.getPedidos();
        // 2. Cria a lista "espelho" que o JavaFX vai usar
        this.observableListPedidos = FXCollections.observableArrayList(pedidosDaComanda);
        // 3. Diz ao ListView para usar essa lista espelho
        this.listaPedidos.setItems(this.observableListPedidos);
        // --- FIM ---

        atualizarTotal(); // (Renomeei o método, só pra ficar mais claro)
    }

    @FXML
    private void adicionarPedido() {
        // (O código de abrir o pop-up é o mesmo)
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/SelecionarProdutoView.fxml"));
//            Parent root = loader.load();
//            SelecionarProdutoController controller = loader.getController();
//            controller.inicializar(this.produtosDisponiveis, this.atendente);
//
//            Stage popupStage = new Stage();
//            popupStage.initModality(Modality.APPLICATION_MODAL);
//            popupStage.setTitle("Adicionar Novo Pedido");
//            popupStage.setScene(new Scene(root));
//            popupStage.showAndWait();
//
//            Pedido novoPedido = controller.getNovoPedido();
//
//            if (novoPedido != null) {
//                // (Lógica de baixar estoque, que já funciona)
//                Produto produtoPedido = novoPedido.getProdutoPedido();
//                int qtd = novoPedido.getQuantidade();
//                if (produtoPedido.getEstoque() < qtd) {
//                    mostrarAlerta("Estoque Insuficiente", "Estoque Atual: " + produtoPedido.getEstoque());
//                    return;
//                }
//                produtoPedido.setEstoque(produtoPedido.getEstoque() - qtd);
//                System.out.println("Baixa estoque: " + produtoPedido.getNome() + " | Novo Estoque: " + produtoPedido.getEstoque());
//
//                // --- MUDANÇA: Adiciona nos DOIS lugares ---
//                // Adiciona na comanda (modelo)
//                this.comanda.adicionarPedido(novoPedido);
//                // Adiciona na lista "espelho" (visual)
//                this.observableListPedidos.add(novoPedido);
//                // --- FIM ---
//
//                atualizarTotal();
//            }
//
//        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void removerPedido() {
        Pedido pedidoSelecionado = listaPedidos.getSelectionModel().getSelectedItem();
        if (pedidoSelecionado == null) {
            mostrarAlerta("Erro", "Selecione um pedido para remover.");
            return;
        }

        // (Lógica de estorno, que agora vai funcionar)
        Produto produtoCancelado = pedidoSelecionado.getProdutoPedido();
        int qtdCancelada = pedidoSelecionado.getQuantidade();
        produtoCancelado.setEstoque(produtoCancelado.getEstoque() + qtdCancelada);
        System.out.println("Estorno estoque: " + produtoCancelado.getNome() + " | Novo Estoque: " + produtoCancelado.getEstoque());

        // --- MUDANÇA: Remove dos DOIS lugares ---
        // Remove da comanda (modelo)
        this.comanda.removerPedido(pedidoSelecionado.getQuantidade()); // Assumindo que removerPedido usa o índice
        int indice = listaPedidos.getSelectionModel().getSelectedIndex();
        this.comanda.removerPedido(indice);

        // Remove da lista "espelho" (visual)
        this.observableListPedidos.remove(pedidoSelecionado);
        // --- FIM ---

        atualizarTotal();
    }

    @FXML
    private void fecharComanda() {
        // (Sem mudanças aqui, a lógica de estoque já saiu)
        comanda.setClienteNome(campoCliente.getText());
        comanda.fechar();
        this.mesa.removerComanda(this.comanda);
        mostrarAlerta("Comanda Fechada", "Comanda fechada com sucesso!\nTotal: R$ " + comanda.calcularTotal());
        Stage stage = (Stage) labelTotal.getScene().getWindow();
        stage.close();
    }

    // Método renomeado de "atualizarTela" para "atualizarTotal"
    private void atualizarTotal() {
        labelTotal.setText(String.format("Total: R$ %.2f", comanda.calcularTotal()));
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
