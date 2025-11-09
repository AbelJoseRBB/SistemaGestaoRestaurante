package App.Controllers;

import Model.Atendimento.Comanda;
import Model.Atendimento.Mesa;
import Model.Atendimento.Pedido;
import Model.Produtos.CategoriaProduto;
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

    //--- Campos FXML ---
    @FXML
    private Label labelTituloComanda;
    @FXML
    private TextField campoCliente;
    @FXML
    private Label labelTotal;

    // Painel da Direita (Pedidos na Comanda)
    @FXML
    private ListView<Pedido> listaPedidos;
    @FXML
    private Button botaoRemPedido;

    // Painel da Esquerda (Seleção de Itens)
    @FXML
    private TabPane tabPaneCategorias;
    @FXML
    private Label labelItemSelecionado;
    @FXML
    private Spinner<Integer> spinnerQuantidade;
    @FXML
    private TextField campoObservacao;
    @FXML
    private Button botaoAddPedido;
    @FXML
    private Button botaoFecharComanda;

    //--- Campos de Dados ---
    private Mesa mesa;
    private Comanda comanda;
    private Usuario atendente;
    private List<ItemVendavel> itensDisponiveis;

    private ObservableList<Pedido> observableListPedidos;
    private Produto produtoSelecionado; // Armazena o produto clicado no grid

    /**
     * Inicializa o controller, carregando dados da comanda e construindo a UI.
     */
    public void carregarComanda(Mesa mesa, Comanda comanda, Usuario atendente, List<ItemVendavel> itens) {
        this.mesa = mesa;
        this.comanda = comanda;
        this.atendente = atendente;
        this.itensDisponiveis = itens;
        this.produtoSelecionado = null; // Garante que nada esteja selecionado no início

        // 1. Configura os campos simples
        labelTituloComanda.setText("Editando " + comanda.toString());
        campoCliente.setText(comanda.getClienteNome());

        // 2. Configura a lista de Pedidos (painel da direita)
        List<Pedido> pedidosDaComanda = this.comanda.getPedidos();
        this.observableListPedidos = FXCollections.observableArrayList(pedidosDaComanda);
        this.listaPedidos.setItems(this.observableListPedidos);

        // 3. Constrói o painel de seleção de produtos (painel da esquerda)
        construirAbasDeProdutos();

        // 4. Calcula o total inicial
        atualizarTotal();
    }

    /**
     * Preenche o TabPane com abas para cada categoria e um grid (TilePane) de
     * botões de produtos dentro de cada aba.
     */
    private void construirAbasDeProdutos() {
        tabPaneCategorias.getTabs().clear();
        labelItemSelecionado.setText("Selecione um item...");

        // 1. Agrupa todos os produtos por sua categoria
        Map<CategoriaProduto, List<Produto>> produtosPorCategoria = new HashMap<>();
        for (ItemVendavel item : this.itensDisponiveis) {
            // Só podemos adicionar 'Produtos' ao menu, 'Servicos' seriam adicionados de outra forma
            if (item instanceof Produto) {
                Produto p = (Produto) item;
                // 'computeIfAbsent' cria a lista se ela não existir e então adiciona o produto
                produtosPorCategoria
                        .computeIfAbsent(p.getCategoria(), k -> new ArrayList<>())
                        .add(p);
            }
        }

        // 2. Cria uma Aba (Tab) para cada categoria no mapa
        for (Map.Entry<CategoriaProduto, List<Produto>> entry : produtosPorCategoria.entrySet()) {
            CategoriaProduto categoria = entry.getKey();
            List<Produto> produtosDaAba = entry.getValue();

            Tab tab = new Tab(categoria.toString()); // Nome da aba (ex: "Bebidas")
            tab.setClosable(false); // Impede que o usuário feche a aba

            // 3. Cria o grid de botões (TilePane)
            TilePane grid = new TilePane();
            grid.setPadding(new Insets(10));
            grid.setHgap(8); // Espaçamento horizontal
            grid.setVgap(8); // Espaçamento vertical

            // 4. Cria um botão para cada produto na categoria
            for (Produto p : produtosDaAba) {
                Button btnProduto = new Button(p.getNome());
                btnProduto.setPrefSize(100, 80); // Tamanho do botão
                btnProduto.setWrapText(true); // Permite que o texto quebre a linha

                // 5. Ação do botão: guarda o produto selecionado e atualiza o label
                btnProduto.setOnAction(e -> {
                    this.produtoSelecionado = p;
                    labelItemSelecionado.setText("Selecionado: " + p.getNome());
                });

                grid.getChildren().add(btnProduto);
            }

            tab.setContent(grid); // Adiciona o grid à aba
            tabPaneCategorias.getTabs().add(tab); // Adiciona a aba ao TabPane
        }
    }

    /**
     * Chamado ao clicar no botão "Adicionar Pedido".
     * Pega o produto selecionado, a quantidade e observação, e o adiciona à comanda.
     */
    @FXML
    private void adicionarPedido() {
        // 1. Valida se um produto foi selecionado (clicado)
        if (this.produtoSelecionado == null) {
            mostrarAlerta("Erro", "Nenhum produto foi selecionado.");
            return;
        }

        int qtd = spinnerQuantidade.getValue();
        String obs = campoObservacao.getText();

        // 2. Cria o novo pedido
        Pedido novoPedido = new Pedido(this.produtoSelecionado, qtd, obs, this.atendente);

        // 3. Lógica de estoque
        if (produtoSelecionado.getEstoque() < qtd) {
            mostrarAlerta("Estoque Insuficiente", "Estoque Atual: " + produtoSelecionado.getEstoque());
            return;
        }
        produtoSelecionado.setEstoque(produtoSelecionado.getEstoque() - qtd);
        System.out.println("Baixa estoque: " + produtoSelecionado.getNome() + " | Novo Estoque: " + produtoSelecionado.getEstoque());

        // 4. Adiciona na comanda (modelo) e na lista (visual)
        this.comanda.adicionarPedido(novoPedido);
        this.observableListPedidos.add(novoPedido);

        // 5. Atualiza o total
        atualizarTotal();

        // 6. Limpa os campos para o próximo pedido
        this.produtoSelecionado = null;
        labelItemSelecionado.setText("Selecione um item...");
        spinnerQuantidade.getValueFactory().setValue(1);
        campoObservacao.clear();
    }

    /**
     * Chamado ao clicar no botão "Remover Pedido Selecionado".
     * Remove o pedido da comanda e devolve o estoque.
     */
    @FXML
    private void removerPedido() {
        Pedido pedidoSelecionado = listaPedidos.getSelectionModel().getSelectedItem();
        if (pedidoSelecionado == null) {
            mostrarAlerta("Erro", "Selecione um pedido para remover.");
            return;
        }

        ItemVendavel itemCancelado = pedidoSelecionado.getItem();

        // Devolve o estoque se o item for um Produto
        if (itemCancelado instanceof Produto) {
            Produto produtoCancelado = (Produto) itemCancelado;
            int qtdCancelada = pedidoSelecionado.getQuantidade();
            produtoCancelado.setEstoque(produtoCancelado.getEstoque() + qtdCancelada);
            System.out.println("Estorno estoque: " + produtoCancelado.getNome() + " | Novo Estoque: " + produtoCancelado.getEstoque());
        }

        // Remove da comanda (modelo)
        int indice = listaPedidos.getSelectionModel().getSelectedIndex();
        this.comanda.removerPedido(indice);

        // Remove da lista (visual)
        this.observableListPedidos.remove(pedidoSelecionado);

        atualizarTotal();
    }

    /**
     * Chamado ao clicar no botão "Fechar Comanda".
     * Salva o nome do cliente, fecha a comanda e navega de volta para a tela GerenciarMesa.
     */
    @FXML
    private void fecharComanda() {
        comanda.setClienteNome(campoCliente.getText());
        comanda.fechar();

        mostrarAlerta("Comanda Fechada", "Comanda fechada com sucesso!\nTotal: R$ " + comanda.calcularTotal());

        // Navega de volta para a tela anterior (GerenciarMesaView)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/GerenciarMesaView.fxml"));
            Parent root = loader.load();

            GerenciarMesaController gerenciarMesaController = loader.getController();
            gerenciarMesaController.inicializar(this.mesa, this.atendente, this.itensDisponiveis);

            Stage stage = (Stage) labelTotal.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gerenciando Mesa " + this.mesa.getNumMesa());

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível voltar para a tela de gerenciamento.");
        }
    }

    /**
     * Recalcula o total da comanda e atualiza o label na tela.
     */
    private void atualizarTotal() {
        labelTotal.setText(String.format("Total: R$ %.2f", comanda.calcularTotal()));
    }
}