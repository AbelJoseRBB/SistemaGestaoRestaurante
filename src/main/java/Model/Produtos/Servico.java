package Model.Produtos;

public class Servico implements ItemVendavel{
    private String nome;
    private double preco;

    public Servico(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    @Override
    public String getNome() { return this.nome; }

    @Override
    public double getPreco() { return this.preco; }
}
