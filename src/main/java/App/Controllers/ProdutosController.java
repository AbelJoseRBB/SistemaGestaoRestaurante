package App.Controllers;

import App.Persistencia.IPersistencia;
import App.Persistencia.PersistenceService;
import Model.Produtos.CategoriaProduto;
import Model.Produtos.Produto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.TilePane;
import javafx.geometry.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ProdutosController extends BaseController{

    @FXML
    private ListView<Produto> listaProdutos;
    @FXML
    private Label labelFormulario;
    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoDescricao;
    @FXML
    private TextField campoPreco;
    @FXML
    private TextField campoEstoque;
    @FXML
    private ComboBox<CategoriaProduto> campoCategoria;

    private IPersistencia persistenceService = new PersistenceService();

    // Esta é a LISTA CENTRAL que vem do MesaController
    private List<Produto> listaProdutosCentral;

    // Esta é a lista que o ListView usa (ela "observa" a lista central)
    private ObservableList<Produto> observableListProdutos;

    private Produto produtoSelecionado = null;

    // O MesaController vai chamar isso para iniciar a tela
    public void inicializar(List<Produto> listaProdutosCentral) {
        this.listaProdutosCentral = listaProdutosCentral;

        // Conecta a lista do ListView com a nossa lista central
        this.observableListProdutos = FXCollections.observableArrayList(listaProdutosCentral);
        this.listaProdutos.setItems(observableListProdutos);

        // Adiciona um "ouvinte" para saber qual item foi clicado
        this.listaProdutos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selecionarProduto(newValue);
                }
        );

        campoCategoria.getItems().setAll(CategoriaProduto.values());

        limparCampos(); // Começa com os campos limpos
    }

    // Chamado quando um produto na lista é clicado
    private void selecionarProduto(Produto produto) {
        this.produtoSelecionado = produto;

        if (produto == null) {
            limparCampos();
            return;
        }

        // Preenche os campos com os dados do produto clicado
        labelFormulario.setText("Editando Produto: " + produto.getNome());
        campoNome.setText(produto.getNome());
        campoDescricao.setText(produto.getDescricao());
        campoPreco.setText(String.format("%.2f", produto.getPreco()));
        campoEstoque.setText(String.valueOf(produto.getEstoque()));
        campoCategoria.setValue(produto.getCategoria());
    }

    @FXML
    private void salvar() {
        try {
            // Pega os dados dos campos
            String nome = campoNome.getText();
            String descricao = campoDescricao.getText();
            double preco = Double.parseDouble(campoPreco.getText().replace(",", "."));
            int estoque = Integer.parseInt(campoEstoque.getText());
            CategoriaProduto categoria = campoCategoria.getValue();

            if (nome == null || nome.trim().isEmpty()) {
                mostrarAlerta("Erro", "O nome do produto é obrigatório.");
                return;
            }

            if (this.produtoSelecionado == null) {
                // Se não há produto selecionado, é um PRODUTO NOVO
                Produto novoProduto = new Produto(nome, descricao, preco, estoque, categoria);

                // Adiciona na lista CENTRAL e na lista VISUAL
                this.listaProdutosCentral.add(novoProduto);
                this.observableListProdutos.add(novoProduto);

            } else {
                // Se há um produto selecionado, é uma EDIÇÃO
                this.produtoSelecionado.setNome(nome); // <-- Assumindo que você tem um setNome()
                this.produtoSelecionado.setDescricao(descricao); // <-- Assumindo que você tem um setDescricao()
                this.produtoSelecionado.setPreco(preco);
                this.produtoSelecionado.setEstoque(estoque);
                this.produtoSelecionado.setCategoria(categoria);

                // Atualiza o item na lista visual
                this.listaProdutos.refresh();
            }
            this.persistenceService.salvarProdutos(this.listaProdutosCentral);
            limparCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Formato", "O Preço (ex: 5.50) e Estoque (ex: 100) devem ser números válidos.");
        } catch (Exception e) {
            mostrarAlerta("Erro", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void removerProduto() {
        if (this.produtoSelecionado == null) {
            mostrarAlerta("Erro", "Nenhum produto selecionado para remover.");
            return;
        }

        // Remove da lista CENTRAL e da lista VISUAL
        this.listaProdutosCentral.remove(this.produtoSelecionado);
        this.observableListProdutos.remove(this.produtoSelecionado);

        this.persistenceService.salvarProdutos(this.listaProdutosCentral);

        limparCampos();
    }

    @FXML
    private void limparCampos() {
        this.produtoSelecionado = null; // Tira a seleção
        this.listaProdutos.getSelectionModel().clearSelection();

        labelFormulario.setText("Adicionar Novo Produto");
        campoNome.clear();
        campoDescricao.clear();
        campoPreco.clear();
        campoEstoque.clear();
        campoCategoria.setValue(null);
    }

}