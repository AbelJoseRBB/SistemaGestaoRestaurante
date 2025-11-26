package Model.Pagamento;

public class PagamentoCartaoDebito extends PagamentoCartao {

    public PagamentoCartaoDebito(double valorDaConta, String bandeira){
        super(valorDaConta,bandeira);
    }

    @Override
    public boolean processar() {
        System.out.println("Processando débito na bandeira " + bandeira);
        this.statusConfirmado = true;
        return true;
    }
    @Override
    public String getTipo() {
        return "Cartão de Débito";
    }

    @Override
    public String gerarComprovante() {
        String statusTexto = this.statusConfirmado ? "APROVADO" : "RECUSADO";

        return "=== COMPROVANTE DÉBITO ===\n" +
                "Bandeira: " + bandeira + "\n" +
                "Valor Total: R$ " + String.format("%.2f", valorDaConta) + "\n" +
                "Transação: APROVADA";
    }
}

