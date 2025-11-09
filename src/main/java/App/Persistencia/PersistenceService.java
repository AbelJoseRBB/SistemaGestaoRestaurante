package App.Persistencia;

import Model.Produtos.CategoriaProduto;
import Model.Sistema.Config;
import Model.Produtos.Produto;
import Model.Usuarios.Garcom; // Importa o Garcom
import Model.Usuarios.Interno; // Importa o Interno
import Model.Usuarios.Usuario; // Importa o Usuario

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray; // Novo: Para ler a lista do arquivo
import com.google.gson.JsonElement; // Novo: Para ler elemento por elemento
import com.google.gson.JsonObject; // Novo: Para ler o objeto JSON
import com.google.gson.JsonParser; // Novo: Para "espiar" o JSON
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

    private static final String CONFIG_FILE = "config.json";
    private static final String USUARIOS_FILE = "usuarios.json"; // <-- NOVO ARQUIVO
    private static final String PRODUTOS_FILE = "produtos.json";
    private static final String CATEGORIAS_FILE = "categorias.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<CategoriaProduto> carregarCategorias() {
        File categoriasFile = new File(CATEGORIAS_FILE);
        if (categoriasFile.exists()) {
            try (Reader reader = new FileReader(categoriasFile)) {

                Type listaTipo = new TypeToken<ArrayList<CategoriaProduto>>() {}.getType();
                List<CategoriaProduto> categorias = gson.fromJson(reader, listaTipo);

                System.out.println("Categorias carregadas de " + CATEGORIAS_FILE);

                // --- A CORREÇÃO ESTÁ AQUI ---
                // Se o arquivo estiver vazio, o GSON retorna null.
                // Nós garantimos que vamos retornar uma lista vazia no lugar.
                if (categorias == null) {
                    return new ArrayList<>();
                } else {
                    return categorias;
                }
                // --- FIM DA CORREÇÃO ---

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
            salvarCategorias(padrao); // Salva o novo arquivo
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

                // 1. Define o "tipo" que queremos: uma Lista de Produtos
                Type listaTipo = new TypeToken<ArrayList<Produto>>() {}.getType();

                // 2. O GSON faz a mágica de carregar a lista inteira de uma vez
                List<Produto> produtos = gson.fromJson(reader, listaTipo);

                System.out.println("Produtos carregados de " + PRODUTOS_FILE);
                return produtos;

            } catch (IOException | IllegalStateException e) {
                System.out.println("Erro ao ler " + PRODUTOS_FILE + ", retornando lista vazia. Erro: " + e.getMessage());
                // Retorna uma lista vazia em caso de erro
                return new ArrayList<>();
            }
        } else {
            System.out.println(PRODUTOS_FILE + " não encontrado, retornando lista vazia.");
            // Retorna uma lista vazia se o arquivo não existe
            return new ArrayList<>();
        }
    }

    @Override
    public List<Usuario> carregarUsuarios() {
        File usuariosFile = new File(USUARIOS_FILE);

        if (usuariosFile.exists()) {
            try (Reader reader = new FileReader(usuariosFile)) {
                // O TRUQUE: Primeiro, lemos o arquivo JSON como uma lista de elementos genéricos
                JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
                List<Usuario> usuarios = new ArrayList<>();

                for (JsonElement element : jsonArray) {
                    JsonObject jsonObject = element.getAsJsonObject();

                    // Verificamos a "dica": se 'acessoConfig' é true
                    boolean isInterno = jsonObject.get("acessoConfig").getAsBoolean();

                    if (isInterno) {
                        // Se for Interno, criamos um objeto Interno (com a classe correta)
                        usuarios.add(gson.fromJson(jsonObject, Interno.class));
                    } else {
                        // Se não, é Garcom
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

        salvarUsuarios(padrao); // Salva o novo arquivo padrão no HD
        return padrao;
    }
}