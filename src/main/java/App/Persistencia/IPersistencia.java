package App.Persistencia;

import Model.Sistema.Config;
import Model.Produtos.Produto;
import Model.Usuarios.Usuario;
import java.util.List;

public interface IPersistencia {
    Config carregarConfig();
    void salvarConfig(Config config);

    List<Usuario> carregarUsuarios();
    void salvarUsuarios(List<Usuario> usuarios);

    List<Produto> carregarProdutos();
    void salvarProdutos(List<Produto> produtos);
}
