    package Model.Pagamento;

    public interface Pagavel {
        boolean processar();
        String getTipo();
    }