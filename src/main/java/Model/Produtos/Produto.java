package Model.Produtos;
import Model.Produtos.CategoriaProduto;

public class Produto implements ItemVendavel {

    private final int id;
    private static int proximoId = 1;
    private String nome;
    private double preco;
    private int estoque;
    private String descricao;
    private String categoriaNome;

    // 3. ATUALIZAR O CONSTRUTOR
    public Produto(String nome, String descricao, double preco, int estoque, String categoriaNome) {
        this.id = proximoId++;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.categoriaNome = categoriaNome; // <-- Linha adicionada
    }

    public double getPreco() {
        return preco;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getId() {
        return id;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setPreco(double preco){
        if(preco < 0)
            System.out.println("ERRO");
        else
            this.preco = preco;

    }

    public void setEstoque(int estoque) {
        if(estoque < 0)
            System.out.println("ERRO");
        else
            this.estoque = estoque;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoriaNome() { // <-- MUDANÇA
        return categoriaNome;
    }

    public void setCategoriaNome(String categoriaNome) { // <-- MUDANÇA
        this.categoriaNome = categoriaNome;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%d] %s - R$ %.2f (estoque: %d)", categoriaNome, id, nome, preco, estoque); // <-- MUDANÇA
    }
}