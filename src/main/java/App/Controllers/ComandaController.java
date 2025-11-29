package App.Controllers;

import Model.Atendimento.Comanda;
import Model.Atendimento.Mesa;
import Model.Atendimento.Pedido;
import Model.Produtos.Produto;
import Model.Usuarios.Usuario;
import Model.Produtos.ItemVendavel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComandaController extends BaseController {

    @FXML
    private Label labelTituloComanda;
    @FXML
    private TextField campoPesquisa;
    @FXML
    private Label labelTotal;
    @FXML
    private ListView<Pedido> listaPedidos;

    @FXML
    private Button botaoRemPedido;
    @FXML
    private Button botaoAddPedido;
    @FXML
    private Button botaoFecharComanda;

    @FXML
    private TabPane tabPaneCategorias;
    @FXML
    private Label labelItemSelecionado;
    @FXML
    private Spinner<Integer> spinnerQuantidade;
    @FXML
    private TextField campoObservacao;

    private Mesa mesa;
    private Comanda comanda;
    private Usuario atendente;
    private List<ItemVendavel> itensDisponiveis;

    private ObservableList<Pedido> observableListPedidos;
    private Produto produtoSelecionado;

    public void carregarComanda(Mesa mesa, Comanda comanda, Usuario atendente, List<ItemVendavel> itens) {
        this.mesa = mesa;
        this.comanda = comanda;
        this.atendente = atendente;
        this.itensDisponiveis = itens;
        this.produtoSelecionado = null;

        labelTituloComanda.setText(comanda.toString());
        campoPesquisa.setText("");
        campoPesquisa.textProperty().addListener((obs, oldValue, newValue) -> {
            construirAbasDeProdutos(newValue);
        });

        List<Pedido> pedidosDaComanda = this.comanda.getPedidos();
        this.observableListPedidos = FXCollections.observableArrayList(pedidosDaComanda);
        this.listaPedidos.setItems(this.observableListPedidos);

        construirAbasDeProdutos("");

        atualizarTotal();
    }

    private void construirAbasDeProdutos(String termoPesquisa) {
        tabPaneCategorias.getTabs().clear();

        if (!termoPesquisa.isEmpty()) {
            this.produtoSelecionado = null;
            labelItemSelecionado.setText("Selecione um item...");
        }
        this.produtoSelecionado = null;
        Map<String, List<Produto>> produtosPorCategoria = new HashMap<>();
        String termo = termoPesquisa.toLowerCase().trim();

        for (ItemVendavel item : this.itensDisponiveis) {
            if (item instanceof Produto) {
                Produto p = (Produto) item;

                if (!termo.isEmpty() && !p.getNome().toLowerCase().contains(termo)) {
                    continue;
                }

                String nomeCat = p.getCategoriaNome();
                if (nomeCat == null || nomeCat.trim().isEmpty()) {
                    continue;
                }

                produtosPorCategoria
                        .computeIfAbsent(nomeCat, k -> new ArrayList<>())
                        .add(p);
            }
        }

        for (Map.Entry<String, List<Produto>> entry : produtosPorCategoria.entrySet()) {
            String nomeCategoria = entry.getKey();
            List<Produto> produtosDaAba = entry.getValue();

            Tab tab = new Tab(nomeCategoria);
            tab.setClosable(false);

            TilePane grid = new TilePane();
            grid.setPadding(new Insets(10));
            grid.setHgap(8);
            grid.setVgap(8);

            for (Produto p : produtosDaAba) {
                Button btnProduto = new Button(p.getNome());
                btnProduto.setPrefSize(100, 80);
                btnProduto.setWrapText(true);

                btnProduto.setOnAction(e -> {
                    this.produtoSelecionado = p;
                    labelItemSelecionado.setText("Selecionado: " + p.getNome());
                });

                grid.getChildren().add(btnProduto);
            }

            tab.setContent(grid);
            tabPaneCategorias.getTabs().add(tab);
        }
    }

    @FXML
    private void adicionarPedido() {
        if (this.produtoSelecionado == null) {
            mostrarAlerta("Erro", "Nenhum produto foi selecionado.");
            return;
        }

        int qtd = spinnerQuantidade.getValue();
        String obs = campoObservacao.getText();

        Pedido novoPedido = new Pedido(this.produtoSelecionado, qtd, obs, this.atendente);

        if (produtoSelecionado.getEstoque() < qtd) {
            mostrarAlerta("Estoque Insuficiente", "Estoque Atual: " + produtoSelecionado.getEstoque());
            return;
        }
        produtoSelecionado.setEstoque(produtoSelecionado.getEstoque() - qtd);
        System.out.println("Baixa estoque: " + produtoSelecionado.getNome() + " | Novo Estoque: " + produtoSelecionado.getEstoque());

        this.comanda.adicionarPedido(novoPedido);
        this.observableListPedidos.add(novoPedido);

        atualizarTotal();

        this.produtoSelecionado = null;
        labelItemSelecionado.setText("Selecione um item...");
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

        ItemVendavel itemCancelado = pedidoSelecionado.getItem();

        if (itemCancelado instanceof Produto) {
            Produto produtoCancelado = (Produto) itemCancelado;
            int qtdCancelada = pedidoSelecionado.getQuantidade();
            produtoCancelado.setEstoque(produtoCancelado.getEstoque() + qtdCancelada);
            System.out.println("Estorno estoque: " + produtoCancelado.getNome() + " | Novo Estoque: " + produtoCancelado.getEstoque());
        }

        int indice = listaPedidos.getSelectionModel().getSelectedIndex();
        this.comanda.removerPedido(indice);

        this.observableListPedidos.remove(pedidoSelecionado);

        atualizarTotal();
    }

    @FXML
    private void fecharComanda() {
        comanda.fechar();

        this.mesa.verificarStatusParaPagamento();

        mostrarAlerta("Comanda Fechada", "Comanda fechada com sucesso!\nTaotal: R$ " + comanda.calcularTotal());

        Stage stage = (Stage) labelTotal.getScene().getWindow();
        stage.close();
    }

    private void atualizarTotal() {
        labelTotal.setText(String.format("Total: R$ %.2f", comanda.calcularTotal()));
    }
}