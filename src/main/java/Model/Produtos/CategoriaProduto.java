package Model.Produtos;

public enum CategoriaProduto {
    BEBIDA("Bebida"),
    PRATO_PRINCIPAL("Prato Principal"),
    SOBREMESA("Sobremesa"),
    PETISCO("Petisco"),
    OUTRO("Outro"); // Categoria padrão

    private final String descricao;

    CategoriaProduto(String descricao) {
        this.descricao = descricao;
    }

    // Isso é útil para mostrar o nome bonito na tela
    @Override
    public String toString() {
        return this.descricao;
    }
}
