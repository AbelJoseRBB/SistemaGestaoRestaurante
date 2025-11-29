package Model.Pagamento;

public abstract class Pagamento implements Pagavel {

    protected double valorDaConta;
    protected boolean statusConfirmado;

    public Pagamento(double valorDaConta) {
        this.valorDaConta = valorDaConta;
        this.statusConfirmado = false;
    }

    public double getValor() {
        return valorDaConta;
    }
    public boolean getStatusConfirmado() {
        return statusConfirmado;
    }


    public abstract String gerarComprovante();


}
