package Model.Atendimento;
import java.util.ArrayList;
import java.util.List;

public class Comanda {

    private static int proximoId = 1;
    private final int id;
    private String clienteNome;
    private boolean fechada;
    private List<Pedido> pedidos;

    public Comanda() {
        this.id = proximoId++;
        this.pedidos = new ArrayList<>();
        this.fechada = false;
        this.clienteNome = "Cliente " + this.id;
    }

    public int getId() {
        return id;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public boolean isFechada() {
        return fechada;
    }

    public void adicionarPedido(Pedido p){
        if(fechada) throw new IllegalArgumentException("Comanda já está fechada");
        pedidos.add(p);
    }

    public boolean removerPedido(int indice){
        if(fechada) return false;
        if(indice < 0 || indice >= pedidos.size()) return  false;
        pedidos.remove(indice);
        return  true;
    }

    public double calcularTotal(){
        double total = 0.0;
        for(Pedido e: pedidos) total += e.getSubtotal();
        return total;
    }

    public void fechar(){
        if(fechada) throw new IllegalStateException("Comando já estava fechada");
        fechada = true;
    }

    public void reabrir() {
        this.fechada = false;
    }

    public List<Pedido> getPedidos(){
        return this.pedidos;
    }

    @Override
    public String toString() {
        return String.format("Comanda %d - Cliente: %s (%s)", id, getClienteNome(), fechada ? "FECHADA" : "ABERTA");
    }
}