package Model.Pagamento;

public class PagamentoDinheiro extends Pagamento {
    public double valorRecebido;

    public PagamentoDinheiro(double valorRecebido, double valorDaConta) {
        super(valorDaConta);
        this.valorRecebido = valorRecebido;
    }

    public double getTroco() {
        return valorRecebido - valorDaConta;
    }

    @Override
    public boolean processar() {
        if (valorRecebido < valorDaConta) {
            this.statusConfirmado = false;
            System.out.println("Erro: pagamento recusado");
            return false;
        } else {
            this.statusConfirmado = true;
            return true;
        }
    }

    @Override
    public String getTipo() {
        return "Dinheiro";
    }

    @Override
    public String gerarComprovante() {
        String statusTexto = this.statusConfirmado ? "APROVADO" : "RECUSADO (Saldo Insuficiente)";

        return "=== PAGAMENTO EM DINHEIRO ===\n" +
                "Total: R$ " + String.format("%.2f", valorDaConta) + "\n" +
                "Recebido: R$ " + String.format("%.2f", valorRecebido) + "\n" +
                "Troco: R$ " + String.format("%.2f", getTroco()) + "\n" +
                "Status: " + statusTexto;
    }
}
