package Model.Sistema;

public class Config {
    private int numeroDeMesas;
    private double taxaDeServico;
    private String nomeRestaurante;
    private String infoRestaurante; // Para CNPJ, Endereço, etc.

    public Config() {
        this.numeroDeMesas = 10; // O padrão que estamos usando
        this.taxaDeServico = 10.0; // Padrão 10%
        this.nomeRestaurante = "Nome do Restaurante";
        this.infoRestaurante = "CNPJ / Endereço / Telefone";
    }

    public int getNumeroDeMesas() {
        return numeroDeMesas;
    }

    public void setNumeroDeMesas(int numeroDeMesas) {
        this.numeroDeMesas = numeroDeMesas;
    }

    public double getTaxaDeServico() {
        return taxaDeServico;
    }

    public void setTaxaDeServico(double taxaDeServico) {
        this.taxaDeServico = taxaDeServico;
    }

    public String getNomeRestaurante() {
        return nomeRestaurante;
    }

    public void setNomeRestaurante(String nomeRestaurante) {
        this.nomeRestaurante = nomeRestaurante;
    }

    public String getInfoRestaurante() {
        return infoRestaurante;
    }

    public void setInfoRestaurante(String infoRestaurante) {
        this.infoRestaurante = infoRestaurante;
    }
}