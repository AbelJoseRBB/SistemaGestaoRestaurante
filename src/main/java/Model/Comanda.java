package Model;
import java.util.ArrayList;
import  java.util.List;

public class Comanda {
    private int numero;
    private String clienteNome;
    private boolean fechada;
    private List<Pedido> pedidos;

    public Comanda(int numero) {
        this.numero = numero;
        this.pedidos = new ArrayList<>();
        this.fechada = false;
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

    public int getNumero() {
        return numero;
    }

    public void adicionarPedido(Pedido p){
        if(fechada) throw new IllegalArgumentException("Comanda já está fechada");
        pedidos.add(p);
    }

    public boolean removerPedido(int indice){
        if(fechada) return false;
        if(indice < 0 || indice > pedidos.size()) return  false;
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

    public List<Pedido> getPedidos(){
        return new ArrayList<>(pedidos);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Comanda #").append(numero).append(fechada ? " (FECHADA)\n" : " (ABERTA)\n");
        for (int i = 0; i < pedidos.size(); i++) {
            sb.append(i).append(": ").append(pedidos.get(i).toString()).append("\n");
        }
        sb.append(String.format("Total: R$ %.2f\n", calcularTotal()));
        return sb.toString();
    }



}