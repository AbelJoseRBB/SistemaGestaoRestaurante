package Model.Atendimento;
import java.util.ArrayList;
import java.util.List;

public class Mesa {
    private int numMesa;
    private List<Comanda> comandas;

    public Mesa(int numMesa){
        this.numMesa = numMesa;
        this.comandas = new ArrayList<>();
    }

    public int getNumMesa() {
        return numMesa;
    }

    public List<Comanda> getComandas() {
        return comandas;
    }

    public boolean isOcupada() {
        return !comandas.isEmpty();
    }

    public void adicionarComanda(Comanda c){
        if (c == null) throw new IllegalArgumentException("Comanda não pode ser nula");
        this.comandas.add(c);
    }

    public void removerComanda(Comanda c){
        if (c == null) return;
        this.comandas.remove(c);
    }

    @Override
    public String toString() {
        return String.format("Mesa %d - %s (%d comandas)", numMesa, isOcupada() ? "Ocupada" : "Livre", comandas.size());
    }
}