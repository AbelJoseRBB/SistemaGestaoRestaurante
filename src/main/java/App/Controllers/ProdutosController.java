package App.Controllers;

import App.Persistencia.IPersistencia;
import Model.Produtos.CategoriaProduto;
import Model.Produtos.Produto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
import java.util.Optional;

public class ProdutosController extends BaseController {

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

    private IPersistencia persistenceService;
    private List<Produto> listaProdutosCentral;
    private ObservableList<Produto> observableListProdutos;
    private List<CategoriaProduto> listaDeCategorias;
    private Produto produtoSelecionado = null;

    /**
     * Inicializa o controller.
     * Este método é chamado pelo MesaController.
     */
    public void inicializar(List<Produto> listaProdutosCentral, IPersistencia service) {
        this.listaProdutosCentral = listaProdutosCentral;
        this.persistenceService = service;

        this.listaDeCategorias = persistenceService.carregarCategorias();
        campoCategoria.getItems().setAll(this.listaDeCategorias);

        this.observableListProdutos = FXCollections.observableArrayList(listaProdutosCentral);
        this.listaProdutos.setItems(observableListProdutos);

        this.listaProdutos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarProduto(newValue)
        );

        limparCampos();
    }

    @FXML
    private void adicionarNovaCategoria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nova Categoria");
        dialog.setHeaderText("Adicionar uma nova categoria de produto");
        dialog.setContentText("Nome da Categoria:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String nome = result.get().trim();

            boolean jaExiste = listaDeCategorias.stream()
                    .anyMatch(cat -> cat.getNome().equalsIgnoreCase(nome));

            if (jaExiste) {
                mostrarAlerta("Erro", "Uma categoria com este nome já existe.");
                return;
            }

            CategoriaProduto novaCategoria = new CategoriaProduto(nome);

            this.listaDeCategorias.add(novaCategoria);
            this.campoCategoria.getItems().add(novaCategoria);

            this.persistenceService.salvarCategorias(this.listaDeCategorias);

            this.campoCategoria.setValue(novaCategoria);
        }
    }

    private void selecionarProduto(Produto produto) {
        this.produtoSelecionado = produto;

        if (produto == null) {
            limparCampos();
            return;
        }

        labelFormulario.setText("Editando Produto: " + produto.getNome());
        campoNome.setText(produto.getNome());
        campoDescricao.setText(produto.getDescricao());
        campoPreco.setText(String.format("%.2f", produto.getPreco()));
        campoEstoque.setText(String.valueOf(produto.getEstoque()));

        CategoriaProduto catDoProduto = listaDeCategorias.stream()
                .filter(cat -> cat.getNome().equals(produto.getCategoriaNome()))
                .findFirst()
                .orElse(null);

        campoCategoria.setValue(catDoProduto);
    }

    @FXML
    private void salvar() {
        try {
            String nome = campoNome.getText();
            String descricao = campoDescricao.getText();
            double preco = Double.parseDouble(campoPreco.getText().replace(",", "."));
            int estoque = Integer.parseInt(campoEstoque.getText());
            CategoriaProduto categoriaSelecionada = campoCategoria.getValue();

            if (nome == null || nome.trim().isEmpty()) {
                mostrarAlerta("Erro", "O nome do produto é obrigatório.");
                return;
            }
            if (categoriaSelecionada == null) {
                mostrarAlerta("Erro", "A categoria é obrigatória.");
                return;
            }

            String nomeCategoria = categoriaSelecionada.getNome();


            if (this.produtoSelecionado == null) {

                Produto novoProduto = new Produto(nome, descricao, preco, estoque, nomeCategoria);

                this.listaProdutosCentral.add(novoProduto);
                this.observableListProdutos.add(novoProduto);

            } else {

                this.produtoSelecionado.setNome(nome);
                this.produtoSelecionado.setDescricao(descricao);
                this.produtoSelecionado.setPreco(preco);
                this.produtoSelecionado.setEstoque(estoque);
                this.produtoSelecionado.setCategoriaNome(nomeCategoria);

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

        this.listaProdutosCentral.remove(this.produtoSelecionado);
        this.observableListProdutos.remove(this.produtoSelecionado);

        this.persistenceService.salvarProdutos(this.listaProdutosCentral);

        limparCampos();
    }

    @FXML
    private void limparCampos() {
        this.produtoSelecionado = null;
        this.listaProdutos.getSelectionModel().clearSelection();

        labelFormulario.setText("Adicionar Novo Produto");
        campoNome.clear();
        campoDescricao.clear();
        campoPreco.clear();
        campoEstoque.clear();
        campoCategoria.setValue(null);
    }
}