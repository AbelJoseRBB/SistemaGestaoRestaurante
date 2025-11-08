package App.Controllers;

import Model.Comanda;
import Model.Mesa;
import Model.Pedido;
import Model.Produtos.Produto;
import Model.Usuarios.Usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    @FXML
    private ListView<Produto> listaProdutosDisponiveis;
    @FXML
    private Spinner<Integer> spinnerQuantidade;
    @FXML
    private TextField campoObservacao;


    private Mesa mesa;
    private Comanda comanda;
    private Usuario atendente;
    private List<Produto> produtosDisponiveis; // Esta lista agora vamos usar para popular o ListView

    private ObservableList<Pedido> observableListPedidos;

    public void carregarComanda(Mesa mesa, Comanda comanda, Usuario atendente, List<Produto> produtos) {
        this.mesa = mesa;
        this.comanda = comanda;
        this.atendente = atendente;
        this.produtosDisponiveis = produtos;

        labelTituloComanda.setText("Editando " + comanda.toString());
        campoCliente.setText(comanda.getClienteNome());

        // Configura a lista de PEDIDOS JÁ FEITOS
        List<Pedido> pedidosDaComanda = this.comanda.getPedidos();
        this.observableListPedidos = FXCollections.observableArrayList(pedidosDaComanda);
        this.listaPedidos.setItems(this.observableListPedidos);

        // =============================================
        // ---   NOVA LINHA   ---
        // Popula a lista de PRODUTOS DISPONÍVEIS (o novo painel da esquerda)
        this.listaProdutosDisponiveis.getItems().addAll(this.produtosDisponiveis);
        // =============================================

        atualizarTotal();
    }

    @FXML
    private void adicionarPedido() {
        // 1. Pega os dados dos campos do FXML (não mais de um pop-up)
        Produto produtoSelecionado = listaProdutosDisponiveis.getSelectionModel().getSelectedItem();
        if (produtoSelecionado == null) {
            mostrarAlerta("Erro", "Nenhum produto foi selecionado.");
            return;
        }

        int qtd = spinnerQuantidade.getValue();
        String obs = campoObservacao.getText();

        // 2. Cria o novo pedido
        // (No seu código antigo, Pedido recebia um 'atendente', estou mantendo)
        Pedido novoPedido = new Pedido(produtoSelecionado, qtd, obs, this.atendente);

        // 3. Lógica de estoque (copiada do seu método antigo)
        if (produtoSelecionado.getEstoque() < qtd) {
            mostrarAlerta("Estoque Insuficiente", "Estoque Atual: " + produtoSelecionado.getEstoque());
            return;
        }
        produtoSelecionado.setEstoque(produtoSelecionado.getEstoque() - qtd);
        System.out.println("Baixa estoque: " + produtoSelecionado.getNome() + " | Novo Estoque: " + produtoSelecionado.getEstoque());

        // 4. Adiciona o pedido na Comanda (modelo) e na Lista (visual)
        this.comanda.adicionarPedido(novoPedido);
        this.observableListPedidos.add(novoPedido);

        // 5. Atualiza o total
        atualizarTotal();

        // 6. (Opcional) Limpa os campos após adicionar
        listaProdutosDisponiveis.getSelectionModel().clearSelection();
        spinnerQuantidade.getValueFactory().setValue(1);
        campoObservacao.clear();
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
        // A lógica original de salvar e fechar a comanda
        comanda.setClienteNome(campoCliente.getText());
        comanda.fechar();

        mostrarAlerta("Comanda Fechada", "Comanda fechada com sucesso!\nTotal: R$ " + comanda.calcularTotal());

        // --- MUDANÇA PARA "VOLTAR" ---
        try {
            // 1. Carrega o FXML da tela ANTERIOR (GerenciarMesaView)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/GerenciarMesaView.fxml"));
            Parent root = loader.load();

            GerenciarMesaController gerenciarMesaController = loader.getController();
            gerenciarMesaController.inicializar(this.mesa, this.atendente, this.produtosDisponiveis);
            Stage stage = (Stage) labelTotal.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gerenciando Mesa " + this.mesa.getNumMesa());

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível voltar para a tela de gerenciamento.");
        }
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
