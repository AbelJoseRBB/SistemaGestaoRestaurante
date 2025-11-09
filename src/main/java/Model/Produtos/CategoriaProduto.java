package Model.Produtos;

public class CategoriaProduto {

    private String nome;

    // Construtor vazio para o GSON
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

    // O toString() é MUITO importante
    // É ele que o ComboBox vai mostrar!
    @Override
    public String toString() {
        return this.nome;
    }
}