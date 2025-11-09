package App.Controllers;

import App.Persistencia.IPersistencia;
import App.Persistencia.PersistenceService;
import Model.Produtos.CategoriaProduto; // Importa a classe Categoria
import Model.Produtos.Produto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class ProdutosController extends BaseController {

    //--- Campos FXML ---
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
    private ComboBox<CategoriaProduto> campoCategoria; // ComboBox de objetos Categoria

    //--- Campos de Dados ---
    private IPersistencia persistenceService;
    private List<Produto> listaProdutosCentral; // A "fonte da verdade"
    private ObservableList<Produto> observableListProdutos; // A lista da UI
    private List<CategoriaProduto> listaDeCategorias; // A "fonte da verdade" das categorias
    private Produto produtoSelecionado = null;

    /**
     * Inicializa o controller.
     * Este método é chamado pelo MesaController.
     */
    public void inicializar(List<Produto> listaProdutosCentral, IPersistencia service) {
        this.listaProdutosCentral = listaProdutosCentral;
        this.persistenceService = service;

        // 1. Carrega as categorias do JSON
        this.listaDeCategorias = persistenceService.carregarCategorias();

        // 2. Popula o ComboBox com a lista de Categorias
        // O ComboBox vai usar o método toString() da Categoria (que retorna o nome)
        campoCategoria.getItems().setAll(this.listaDeCategorias);

        // 3. Configura a lista de produtos (ListView)
        this.observableListProdutos = FXCollections.observableArrayList(listaProdutosCentral);
        this.listaProdutos.setItems(observableListProdutos);

        // 4. Adiciona um "ouvinte" para saber qual item foi clicado
        this.listaProdutos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarProduto(newValue)
        );

        limparCampos(); // Começa com os campos limpos
    }

    /**
     * Chamado ao clicar no botão "+" ao lado das categorias.
     * Abre um pop-up para criar uma nova categoria.
     */
    @FXML
    private void adicionarNovaCategoria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nova Categoria");
        dialog.setHeaderText("Adicionar uma nova categoria de produto");
        dialog.setContentText("Nome da Categoria:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String nome = result.get().trim();

            // Verifica se a categoria já existe
            boolean jaExiste = listaDeCategorias.stream()
                    .anyMatch(cat -> cat.getNome().equalsIgnoreCase(nome));

            if (jaExiste) {
                mostrarAlerta("Erro", "Uma categoria com este nome já existe.");
                return;
            }

            // Cria a nova categoria
            CategoriaProduto novaCategoria = new CategoriaProduto(nome);

            // Adiciona nas listas (a de dados e a do ComboBox)
            this.listaDeCategorias.add(novaCategoria);
            this.campoCategoria.getItems().add(novaCategoria);

            // Salva a lista de categorias de volta no JSON
            this.persistenceService.salvarCategorias(this.listaDeCategorias);

            // Seleciona a categoria que acabou de ser criada
            this.campoCategoria.setValue(novaCategoria);
        }
    }

    /**
     * Chamado quando um produto na lista é clicado.
     * Preenche o formulário com os dados do produto.
     */
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

        // Encontra o *objeto* Categoria que corresponde ao *nome* salvo no produto
        CategoriaProduto catDoProduto = listaDeCategorias.stream()
                .filter(cat -> cat.getNome().equals(produto.getCategoriaNome()))
                .findFirst()
                .orElse(null); // Retorna null se a categoria foi deletada

        campoCategoria.setValue(catDoProduto);
    }

    /**
     * Salva um produto (novo ou editado) e atualiza o JSON.
     */
    @FXML
    private void salvar() {
        try {
            // Pega os dados dos campos
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

            // Pega o *nome* da categoria para salvar no produto
            String nomeCategoria = categoriaSelecionada.getNome();


            if (this.produtoSelecionado == null) {
                // Se não há produto selecionado, é um PRODUTO NOVO
                Produto novoProduto = new Produto(nome, descricao, preco, estoque, nomeCategoria);

                // Adiciona na lista CENTRAL e na lista VISUAL
                this.listaProdutosCentral.add(novoProduto);
                this.observableListProdutos.add(novoProduto);

            } else {
                // Se há um produto selecionado, é uma EDIÇÃO
                this.produtoSelecionado.setNome(nome);
                this.produtoSelecionado.setDescricao(descricao);
                this.produtoSelecionado.setPreco(preco);
                this.produtoSelecionado.setEstoque(estoque);
                this.produtoSelecionado.setCategoriaNome(nomeCategoria); // Salva o nome

                // Atualiza o item na lista visual
                this.listaProdutos.refresh();
            }

            // Salva a lista inteira no arquivo JSON
            this.persistenceService.salvarProdutos(this.listaProdutosCentral);

            limparCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Formato", "O Preço (ex: 5.50) e Estoque (ex: 100) devem ser números válidos.");
        } catch (Exception e) {
            mostrarAlerta("Erro", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * Remove o produto selecionado da lista e salva a alteração no JSON.
     */
    @FXML
    private void removerProduto() {
        if (this.produtoSelecionado == null) {
            mostrarAlerta("Erro", "Nenhum produto selecionado para remover.");
            return;
        }

        // Remove da lista CENTRAL e da lista VISUAL
        this.listaProdutosCentral.remove(this.produtoSelecionado);
        this.observableListProdutos.remove(this.produtoSelecionado);

        // Salva a lista atualizada no arquivo JSON
        this.persistenceService.salvarProdutos(this.listaProdutosCentral);

        limparCampos();
    }

    /**
     * Limpa o formulário para adicionar um novo item.
     */
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