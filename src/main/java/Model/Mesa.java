package Model;
public class Mesa {

    private int numMesa;
    private boolean ocupada;
    private Comanda comanda;

    public Mesa(int numMesa){
        this.numMesa = numMesa;
        this.ocupada = false;
        this.comanda = null;
    }

    public int getNumMesa() {
        return numMesa;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void ocupar(Comanda c){
        if(ocupada) throw  new IllegalStateException("Mesa já está ocupada");
        this.comanda = c;
        this.ocupada = true;
    }

    public Comanda liberar(){
        Comanda old = this.comanda;
        this.comanda = null;
        this.ocupada = false;
        return old;
    }

    @Override
    public String toString() {
        return String.format("Mesa %d - %s", numMesa, ocupada ? "Ocupada" : "Livre");
    }
}