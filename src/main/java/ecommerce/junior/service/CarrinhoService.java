package ecommerce.junior.service;

import ecommerce.junior.model.Carrinho;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.CarrinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CarrinhoService {


    @Autowired
    private CarrinhoRepository carrinhoRepository;


    public Carrinho buscarCarrinhoPorCliente(Cliente cliente) {
        return carrinhoRepository.findByClienteId(cliente.getId())
                .orElseGet(() -> criarNovoCarrinho(cliente));
    }

    private Carrinho criarNovoCarrinho(Cliente cliente) {
        Carrinho carrinho = new Carrinho();
        carrinho.setCliente(cliente);
        return carrinhoRepository.save(carrinho);
    }


    public void adicionarProduto(Cliente cliente, Produto produto) {
        Carrinho carrinho = buscarCarrinhoPorCliente(cliente);

        carrinho.getProdutos().merge(produto, 1, Integer::sum); // Incrementa a quantidade se o produto já existir, ou adiciona com quantidade 1.
        carrinhoRepository.save(carrinho);
    }


    public void removerProduto(Cliente cliente, Produto produto) {
        Carrinho carrinho = buscarCarrinhoPorCliente(cliente);

        if (carrinho.getProdutos().containsKey(produto)) {
            int quantidadeAtual = carrinho.getProdutos().get(produto);
            if (quantidadeAtual > 1) {
                carrinho.getProdutos().put(produto, quantidadeAtual - 1);
            } else { // se a quantidade for maior que 1, reduz. Se for 1, remove o produto.
                carrinho.getProdutos().remove(produto);
            }
        }

        carrinhoRepository.save(carrinho);
    }

    public void atualizarQuantidade(Cliente cliente, Produto produto, int quantidade) {
        Carrinho carrinho = buscarCarrinhoPorCliente(cliente);

        if (quantidade > 0) {
            carrinho.getProdutos().put(produto, quantidade); // Atualiza a quantidade para o valor informado.
        } else {
            carrinho.getProdutos().remove(produto); // Remove o produto se a quantidade for zero ou menor.
        }

        carrinhoRepository.save(carrinho);
    }


    public Map<Produto, Integer> listarProdutosComQuantidades(Cliente cliente) {
        Carrinho carrinho = buscarCarrinhoPorCliente(cliente);
        return carrinho.getProdutos();
    }

    public void salvarCarrinho(Carrinho carrinho) {
        carrinhoRepository.save(carrinho);
    }

    public Carrinho getCarrinhoByClienteId(Cliente cliente) {
        Carrinho carrinho = carrinhoRepository.findByClienteId(cliente.getId()).orElse(null);
        return carrinho;
    }
}
