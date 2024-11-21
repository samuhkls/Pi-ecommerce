package ecommerce.junior.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Carrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "carrinho_produto",
            joinColumns = @JoinColumn(name = "carrinho_id")
    )
    @MapKeyColumn(name = "produto_id") // Apenas IDs dos produtos
    @Column(name = "quantidade")

    private Map<Long, Integer> produtos = new HashMap<>();

    @ElementCollection
    @CollectionTable(
            name = "carrinho_produto_preco",
            joinColumns = @JoinColumn(name = "carrinho_id")
    )
    @MapKeyColumn(name = "produto_id")
    @Column(name = "preco")
    private Map<Long, Double> precos = new HashMap<>();

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "total")
    private Double total = 0.0;

    public void adicionarProduto(Long produtoId, int quantidade, Double preco) {
        produtos.put(produtoId, produtos.getOrDefault(produtoId, 0) + quantidade);
        precos.put(produtoId, preco);
        atualizarTotal();
    }

    public void removerProduto(Long produtoId) {
        produtos.remove(produtoId);
        precos.remove(produtoId);
        atualizarTotal();
    }

    public void atualizarQuantidade(Long produtoId, int quantidade) {
        if (produtos.containsKey(produtoId)) {
            if (quantidade > 0) {
                produtos.put(produtoId, quantidade);
            } else {
                removerProduto(produtoId);
            }
            atualizarTotal();
        }
    }

    private void atualizarTotal() {
        total = produtos.entrySet().stream()
                .map(entry -> precos.get(entry.getKey()) * entry.getValue())
                .reduce(0.0, Double::sum);
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Map<Long, Integer> getProdutos() {
        return produtos;
    }

    public void setProdutos(Map<Long, Integer> produtos) {
        this.produtos = produtos;
    }

    public Map<Long, Double> getPrecos() {
        return precos;
    }

    public void setPrecos(Map<Long, Double> precos) {
        this.precos = precos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
