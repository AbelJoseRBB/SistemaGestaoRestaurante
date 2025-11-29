package Model.Pagamento;

public abstract class PagamentoCartao extends Pagamento{
    protected String bandeira;

    public PagamentoCartao(double valorDaConta,String bandeira){
        super(valorDaConta);
        this.bandeira = bandeira;
    }
    public String getBandeira() {
        return bandeira;
    }
}
