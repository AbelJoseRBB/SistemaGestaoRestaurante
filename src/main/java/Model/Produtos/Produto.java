package Model.Produtos;
public class Produto {

    private final int id;
    private static int proximoId = 1;
    private String nome;
    private double preco;
    private int estoque; // deixar assim por equanto
    private String descricao;

    public Produto(String nome, String descricao, double preco, int estoque){
        this.id = proximoId++;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
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

    @Override
    public String toString() {
        return String.format("[%d] %s - R$ %.2f (estoque: %d)", id, nome, preco, estoque);
    }
}
