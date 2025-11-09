package Model.Usuarios;

public abstract class Usuario {

    private String nome, senha;
    protected boolean acessoConfig = false;

    public Usuario() {
    }

    public Usuario(String nome, String senha){
        this.nome = nome;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean autenticar(String nome, String senha){
        return (this.nome != null && this.senha != null) && (this.nome.equals(nome) && this.senha.equals(senha));
    }

    public boolean hasAcessoEstoque(){return acessoConfig;}

    @Override
    public String toString() {
        String tipo = this.getClass().getSimpleName();
        return String.format("%s (%s)", this.nome, tipo);
    }

}