package ecommerce.junior.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "carrinho_produto",
            joinColumns = @JoinColumn(name = "carrinho_id")
    )
    @MapKeyJoinColumn(name = "produto_id")
    @Column(name = "quantidade")
    private Map<Produto, Integer> produtos = new HashMap<>();

    @OneToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    private Cliente cliente;


    public void adicionarProduto(Produto produto, int quantidade) {
        produtos.put(produto, produtos.getOrDefault(produto, 0) + quantidade);
    }

    public void removerProduto(Produto produto) {
        produtos.remove(produto);
    }

    public void atualizarQuantidade(Produto produto, int quantidade) {
        if (produtos.containsKey(produto)) {
            if (quantidade > 0) {
                produtos.put(produto, quantidade);
            } else {
                produtos.remove(produto);
            }
        }
    }

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

    public Map<Produto, Integer> getProdutos() {
        return new HashMap<>(produtos); // Converte PersistentMap
    }

    public void setProdutos(Map<Produto, Integer> produtos) {
        this.produtos = produtos;
    }

    // getters e setters
}
