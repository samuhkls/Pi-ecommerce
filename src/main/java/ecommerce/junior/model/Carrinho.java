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

    @OneToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    private Cliente cliente;


    public void adicionarProduto(Long produtoId, int quantidade) {
        produtos.put(produtoId, produtos.getOrDefault(produtoId, 0) + quantidade);
    }



    public void removerProduto(Produto produto) {
        produtos.remove(produto);
    }

    public void atualizarQuantidade(Produto produto, int quantidade) {
        if (produtos.containsKey(produto)) {
            if (quantidade > 0) {
                produtos.put(produto.getId(), quantidade);
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

    public Map<Long, Integer> getProdutos() {
        return produtos;
    }

    public void setProdutos(Map<Long, Integer> produtos) {
        this.produtos = produtos;
    }


    // getters e setters
}
