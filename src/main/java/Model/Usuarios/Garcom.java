package Model.Usuarios;

public class Garcom extends Usuario{

    public Garcom(){
        super();
        this.acessoConfig = false;
    }

    public Garcom(String nome, String senha){
        super(nome, senha);
        this.acessoConfig = false;
    }


}