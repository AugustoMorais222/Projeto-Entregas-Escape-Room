package br.edu.entregas.model;

public class Mercadoria {
    private final long id;
    private final String nome;
    private final String descricao;
    private final double peso;
    private final double valor;
    private final String status;
    private final Endereco enderecoEntrega;

    public Mercadoria(long id, String nome, String descricao, double peso,
                      double valor, String status, Endereco enderecoEntrega) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.peso = peso;
        this.valor = valor;
        this.status = status;
        this.enderecoEntrega = enderecoEntrega;
    }

    public long getId() { return id; }
    public String getNome() { return nome; }
    public Endereco getEnderecoEntrega() { return enderecoEntrega; }

    @Override
    public String toString() {
        return "#" + id + " | " + nome + " | " + descricao + " | " + peso +
               " kg | R$ " + String.format("%.2f", valor) + " | " + status +
               " | Entrega: " + enderecoEntrega;
    }
}
