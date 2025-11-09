package App.Controllers;

import App.Persistencia.IPersistencia;
import Model.Sistema.Config;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ConfigController extends BaseController{

    @FXML
    private TextField campoNumMesas;
    @FXML
    private TextField campoTaxaServico;
    @FXML
    private TextField campoNomeRestaurante;
    @FXML
    private TextField campoInfoRestaurante;

    private Config configAtual;

    // --- MUDANÇA: Recebe o Service ---
    private IPersistencia persistenceService;
    // --- FIM DA MUDANÇA ---

    // --- MUDANÇA: Assinatura do método mudou ---
    public void inicializar(Config config, IPersistencia service) {
        this.configAtual = config;
        this.persistenceService = service; // Guarda o service
        carregarDadosNaTela();
    }

    // Pega os dados do objeto Config e bota nos campos de texto
    private void carregarDadosNaTela() {
        campoNumMesas.setText(String.valueOf(configAtual.getNumeroDeMesas()));
        campoTaxaServico.setText(String.format("%.2f", configAtual.getTaxaDeServico()));
        campoNomeRestaurante.setText(configAtual.getNomeRestaurante());
        campoInfoRestaurante.setText(configAtual.getInfoRestaurante());
    }

    @FXML
    private void salvarConfiguracoes() {
        try {
            // 1. Pega os dados da tela
            int numMesas = Integer.parseInt(campoNumMesas.getText());
            double taxa = Double.parseDouble(campoTaxaServico.getText().replace(",", "."));
            String nome = campoNomeRestaurante.getText();
            String info = campoInfoRestaurante.getText();

            // 2. Atualiza o objeto Config "vivo"
            this.configAtual.setNumeroDeMesas(numMesas);
            this.configAtual.setTaxaDeServico(taxa);
            this.configAtual.setNomeRestaurante(nome);
            this.configAtual.setInfoRestaurante(info);

            // --- MUDANÇA: Manda o Service SALVAR NO HD ---
            this.persistenceService.salvarConfig(this.configAtual);

            mostrarAlerta("Sucesso", "Configurações salvas com sucesso no arquivo config.json!");

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Formato", "Número de Mesas (ex: 10) e Taxa (ex: 10.0) devem ser números válidos.");
        } catch (Exception e) {
            mostrarAlerta("Erro", "Ocorreu um erro: " + e.getMessage());
        }
    }
}