package Model.Atendimento;
import java.util.ArrayList;
import java.util.List;

public class Mesa {
    private int numMesa;
    private List<Comanda> comandas;
    private boolean aguardandoPagamento;

    public Mesa(int numMesa){
        this.numMesa = numMesa;
        this.comandas = new ArrayList<>();
        this.aguardandoPagamento = false;
    }

    public int getNumMesa() {
        return numMesa;
    }

    public List<Comanda> getComandas() {
        return comandas;
    }

    public void adicionarComanda(Comanda c){
        if (c == null) throw new IllegalArgumentException("Comanda não pode ser nula");
        this.comandas.add(c);
    }

    public void removerComanda(Comanda c){
        if (c == null) return;
        this.comandas.remove(c);
    }

    public enum StatusMesa {
        LIVRE,
        OCUPADA,
        AGUARDANDO_PAGAMENTO
    }


    public boolean isOcupada() {
        return !this.comandas.isEmpty();
    }

    public boolean isAguardandoPagamento() {
        return aguardandoPagamento;
    }

    public void setAguardandoPagamento(boolean aguardandoPagamento) {
        this.aguardandoPagamento = aguardandoPagamento;
    }
    // Em Mesa.java

    /**
     * Verifica se todas as comandas filhas estão fechadas.
     * Se sim, marca a mesa como 'aguardandoPagamento'.
     * Se pelo menos uma estiver aberta, desmarca.
     */
    public void verificarStatusParaPagamento() {
        // 1. Se não há comandas, não pode estar aguardando pagamento.
        if (this.comandas.isEmpty()) {
            this.aguardandoPagamento = false;
            return;
        }

        // 2. Verifica se TODAS as comandas da lista estão fechadas.
        for (Comanda c : this.comandas) {
            if (!c.isFechada()) {
                // Encontrou uma comanda aberta, então a mesa NÃO está aguardando pagamento.
                this.aguardandoPagamento = false;
                return;
            }
        }

        // 3. Se o 'for' terminou, é porque TODAS estão fechadas.
        // Então, a mesa inteira deve ser marcada como Aguardando Pagamento.
        this.aguardandoPagamento = true;
    }

    @Override
    public String toString() {
        return String.format("Mesa %d - %s (%d comandas)", numMesa, isOcupada() ? "Ocupada" : "Livre", comandas.size());
    }
}