package Model.Produtos;

public class CategoriaProduto {

    private String nome;

    public CategoriaProduto() {}

    public CategoriaProduto(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}