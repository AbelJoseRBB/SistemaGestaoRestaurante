package App.Controllers;

import Model.Config;
import Model.Usuarios.Garcom; // Importa o Garcom
import Model.Usuarios.Interno; // Importa o Interno
import Model.Usuarios.Usuario; // Importa o Usuario

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray; // Novo: Para ler a lista do arquivo
import com.google.gson.JsonElement; // Novo: Para ler elemento por elemento
import com.google.gson.JsonObject; // Novo: Para ler o objeto JSON
import com.google.gson.JsonParser; // Novo: Para "espiar" o JSON

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class PersistenceService {

    private static final String CONFIG_FILE = "config.json";
    private static final String USUARIOS_FILE = "usuarios.json"; // <-- NOVO ARQUIVO

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // --- LÓGICA DAS CONFIGURAÇÕES (SEM MUDANÇAS) ---

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

    public void salvarConfig(Config config) {
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(config, writer);
            System.out.println("Configurações salvas em " + CONFIG_FILE);
        } catch (IOException e) {
            System.out.println("Erro ao salvar config.json: " + e.getMessage());
        }
    }

    // --- LÓGICA DE USUÁRIOS (NOVOS MÉTODOS) ---

    /**
     * Tenta carregar a lista de Usuários do arquivo "usuarios.json".
     * Se o arquivo não existir, cria a lista padrão (joao, admin).
     */
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

    /**
     * Salva a lista de Usuários de volta no arquivo "usuarios.json".
     */
    public void salvarUsuarios(List<Usuario> usuarios) {
        try (Writer writer = new FileWriter(USUARIOS_FILE)) {
            gson.toJson(usuarios, writer);
            System.out.println("Usuários salvos em " + USUARIOS_FILE);
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuarios.json: " + e.getMessage());
        }
    }

    /**
     * Cria a lista padrão de usuários se o arquivo não existir.
     */
    private List<Usuario> criarUsuariosPadraoESalvar() {
        List<Usuario> padrao = new ArrayList<>();
        padrao.add(new Garcom("joao", "123"));
        padrao.add(new Interno("admin", "admin"));

        salvarUsuarios(padrao); // Salva o novo arquivo padrão no HD
        return padrao;
    }
}