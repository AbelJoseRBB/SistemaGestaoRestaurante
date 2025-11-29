package App.Persistencia;

import Model.Produtos.CategoriaProduto;
import Model.Sistema.Config;
import Model.Produtos.Produto;
import Model.Usuarios.Garcom;
import Model.Usuarios.Interno;
import Model.Usuarios.Usuario;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class PersistenceService implements IPersistencia {

    private static final String CONFIG_FILE = "Dados/config.json";
    private static final String USUARIOS_FILE = "Dados/usuarios.json";
    private static final String PRODUTOS_FILE = "Dados/produtos.json";
    private static final String CATEGORIAS_FILE = "Dados/categorias.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<CategoriaProduto> carregarCategorias() {
        File categoriasFile = new File(CATEGORIAS_FILE);
        if (categoriasFile.exists()) {
            try (Reader reader = new FileReader(categoriasFile)) {

                Type listaTipo = new TypeToken<ArrayList<CategoriaProduto>>() {}.getType();
                List<CategoriaProduto> categorias = gson.fromJson(reader, listaTipo);

                System.out.println("Categorias carregadas de " + CATEGORIAS_FILE);

                if (categorias == null) {
                    return new ArrayList<>();
                } else {
                    return categorias;
                }

            } catch (IOException | IllegalStateException e) {
                System.out.println("Erro ao ler " + CATEGORIAS_FILE);
                return new ArrayList<>(); // Retorna vazia (Correto)
            }
        } else {
            System.out.println(CATEGORIAS_FILE + " não encontrado, criando lista padrão.");
            List<CategoriaProduto> padrao = new ArrayList<>();
            padrao.add(new CategoriaProduto("Bebidas"));
            padrao.add(new CategoriaProduto("Pratos"));
            padrao.add(new CategoriaProduto("Sobremesas"));
            salvarCategorias(padrao);
            return padrao;
        }
    }

    public void salvarCategorias(List<CategoriaProduto> categorias) {
        try (Writer writer = new FileWriter(CATEGORIAS_FILE)) {
            gson.toJson(categorias, writer);
            System.out.println("Categorias salvas em " + CATEGORIAS_FILE);
        } catch (IOException e) {
            System.out.println("Erro ao salvar " + CATEGORIAS_FILE + ": " + e.getMessage());
        }
    }

    @Override
    public Config carregarConfig() {
        File configFile = new File(CONFIG_FILE);

        if (configFile.exists()) {
            try (Reader reader = new FileReader(configFile)) {
                Config config = gson.fromJson(reader, Config.class);
                System.out.println("Configurações carregadas de " + CONFIG_FILE);
                return config;
            } catch (IOException e) {
                System.out.println("Erro ao ler config.json, usando padrão. Erro: " + e.getMessage());
                return new Config();
            }
        } else {
            System.out.println(CONFIG_FILE + " não encontrado, criando um novo com valores padrão.");
            Config configPadrao = new Config();
            salvarConfig(configPadrao);
            return configPadrao;
        }
    }

    @Override
    public void salvarConfig(Config config) {
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(config, writer);
            System.out.println("Configurações salvas em " + CONFIG_FILE);
        } catch (IOException e) {
            System.out.println("Erro ao salvar config.json: " + e.getMessage());
        }
    }

    @Override
    public void salvarProdutos(List<Produto> produtos) {
        try (Writer writer = new FileWriter(PRODUTOS_FILE)) {
            gson.toJson(produtos, writer);
            System.out.println("Produtos salvos em " + PRODUTOS_FILE);
        } catch (IOException e) {
            System.out.println("Erro ao salvar " + PRODUTOS_FILE + ": " + e.getMessage());
        }
    }

    @Override
    public List<Produto> carregarProdutos() {
        File produtoFile = new File(PRODUTOS_FILE);
        if (produtoFile.exists()) {
            try (Reader reader = new FileReader(produtoFile)) {

                Type listaTipo = new TypeToken<ArrayList<Produto>>() {}.getType();

                List<Produto> produtos = gson.fromJson(reader, listaTipo);

                System.out.println("Produtos carregados de " + PRODUTOS_FILE);
                return produtos;

            } catch (IOException | IllegalStateException e) {
                System.out.println("Erro ao ler " + PRODUTOS_FILE + ", retornando lista vazia. Erro: " + e.getMessage());
                return new ArrayList<>();
            }
        } else {
            System.out.println(PRODUTOS_FILE + " não encontrado, retornando lista vazia.");
            return new ArrayList<>();
        }
    }

    @Override
    public List<Usuario> carregarUsuarios() {
        File usuariosFile = new File(USUARIOS_FILE);

        if (usuariosFile.exists()) {
            try (Reader reader = new FileReader(usuariosFile)) {
                JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
                List<Usuario> usuarios = new ArrayList<>();

                for (JsonElement element : jsonArray) {
                    JsonObject jsonObject = element.getAsJsonObject();

                    boolean isInterno = jsonObject.get("acessoConfig").getAsBoolean();

                    if (isInterno) {
                        usuarios.add(gson.fromJson(jsonObject, Interno.class));
                    } else {
                        usuarios.add(gson.fromJson(jsonObject, Garcom.class));
                    }
                }
                System.out.println("Usuários carregados de " + USUARIOS_FILE);
                return usuarios;

            } catch (IOException | IllegalStateException e) {
                System.out.println("Erro ao ler " + USUARIOS_FILE + ", usando padrão. Erro: " + e.getMessage());
                return criarUsuariosPadraoESalvar();
            }
        } else {
            System.out.println(USUARIOS_FILE + " não encontrado, criando padrão.");
            return criarUsuariosPadraoESalvar();
        }
    }

    @Override
    public void salvarUsuarios(List<Usuario> usuarios) {
        try (Writer writer = new FileWriter(USUARIOS_FILE)) {
            gson.toJson(usuarios, writer);
            System.out.println("Usuários salvos em " + USUARIOS_FILE);
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuarios.json: " + e.getMessage());
        }
    }


    private List<Usuario> criarUsuariosPadraoESalvar() {
        List<Usuario> padrao = new ArrayList<>();
        padrao.add(new Interno("admin", "admin"));

        salvarUsuarios(padrao);
        return padrao;
    }
}