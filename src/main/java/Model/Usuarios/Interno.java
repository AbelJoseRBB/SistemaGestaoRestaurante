package Model.Usuarios;

public class Interno extends Usuario{

    public Interno() {
        super();
        this.acessoConfig = true;
    }

    public Interno(String nome, String senha){
        super(nome, senha);
        this.acessoConfig = true;
    }

}