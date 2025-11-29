package Model.Pagamento;

public class PagamentoCartaoCredito extends PagamentoCartao {
    private int numeroParcelas;

    public PagamentoCartaoCredito(double valor, String bandeira, int numeroParcelas) {
        super(valor, bandeira);
        this.numeroParcelas = numeroParcelas;
    }

    @Override
    public boolean processar() {
        System.out.println("Processando crédito " + bandeira + " em " + numeroParcelas + "x");
        this.statusConfirmado = true;
        return true;
    }

    @Override
    public String getTipo() {

        return "Cartão de Crédito";
    }

    // Polimorfismo: Mostra as parcelas detalhadas
    @Override
    public String gerarComprovante() {
        String statusTexto = this.statusConfirmado ? "APROVADO" : "RECUSADO";
        double valorParcela = valorDaConta / numeroParcelas;

        return "=== COMPROVANTE CRÉDITO ===\n" +
                "Bandeira: " + bandeira + "\n" +
                "Valor Total: R$ " + String.format("%.2f", valorDaConta) + "\n" +
                "Condição: " + numeroParcelas + "x de R$ " + String.format("%.2f", valorParcela) + "\n" +
                "Transação: APROVADA";
    }

}
