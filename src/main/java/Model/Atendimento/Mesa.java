package Model.Atendimento;
import java.util.ArrayList;
import java.util.List;

public class Mesa {

    private int numMesa;

    // --- A GRANDE MUDANÇA ---
    // Trocamos um objeto Comanda por uma LISTA de Comandas
    private List<Comanda> comandas;
    // O status "ocupada" agora é controlado pela lista
    // private boolean ocupada; // REMOVIDO
    // private Comanda comanda; // REMOVIDO
    // --- FIM DA MUDANÇA ---


    public Mesa(int numMesa){
        this.numMesa = numMesa;
        this.comandas = new ArrayList<>(); // Inicializa a lista
    }

    public int getNumMesa() {
        return numMesa;
    }

    // Retorna a lista inteira
    public List<Comanda> getComandas() {
        return comandas;
    }

    // Agora "isOcupada" checa se a lista tem algo
    public boolean isOcupada() {
        return !comandas.isEmpty();
    }

    // Renomeado de "ocupar" para "adicionarComanda"
    public void adicionarComanda(Comanda c){
        if (c == null) throw new IllegalArgumentException("Comanda não pode ser nula");
        this.comandas.add(c);
    }

    // Renomeado de "liberar" para "removerComanda"
    // Agora ele remove uma comanda específica
    public void removerComanda(Comanda c){
        if (c == null) return;
        this.comandas.remove(c);
    }

    @Override
    public String toString() {
        return String.format("Mesa %d - %s (%d comandas)",
                numMesa,
                isOcupada() ? "Ocupada" : "Livre",
                comandas.size());
    }
}