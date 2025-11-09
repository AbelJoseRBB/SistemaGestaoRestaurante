package Model.Atendimento;
import java.util.Date;

import Model.Produtos.ItemVendavel;
import Model.Usuarios.Usuario;

public class Pedido {
    private Usuario atendente;
    private ItemVendavel item;
    private int quantidade;
    private String observacao;
    private Date horario;

    public Pedido(ItemVendavel item, int quantidade, String observacao, Usuario atendente){
        if(quantidade <= 0 ) throw new IllegalArgumentException("Quantidade invalida");
        this.item = item;
        this.quantidade = quantidade;
        this.observacao = observacao;
        this.atendente = atendente;
        this.horario = new Date();
    }

    public Usuario getAtendente(){
        return atendente;
    }

    public ItemVendavel getItem() {
        return item;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public Date getHorario() {
        return horario;
    }

    public double getSubtotal(){
        return  item.getPreco() * quantidade;
    }

    @Override
    public String toString() {
        return String.format("%s x%d = R$ %.2f %s", item.getNome(), quantidade, getSubtotal(),
                (observacao == null || observacao.isEmpty()) ? "" : ("(" + observacao + ")"));
    }


}