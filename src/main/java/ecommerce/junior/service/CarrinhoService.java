package ecommerce.junior.service;

import ecommerce.junior.model.Carrinho;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.CarrinhoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public Carrinho obterOuCriarCarrinho(HttpSession session) {
        Long carrinhoId = (Long) session.getAttribute("carrinhoId");
        Carrinho carrinho;

        if (carrinhoId != null) {
            carrinho = carrinhoRepository.findById(carrinhoId).orElse(null);
        } else {
            carrinho = new Carrinho();
            carrinhoRepository.save(carrinho);
            session.setAttribute("carrinhoId", carrinho.getId());
        }

        return carrinho;
    }

    public void adicionarProduto(Cliente cliente, Produto produto) {
        Carrinho carrinho = buscarCarrinhoPorCliente(cliente);
        carrinho.getProdutos().merge(produto.getId(), 1, Integer::sum);
        carrinhoRepository.save(carrinho);
    }

    public void removerProduto(Cliente cliente, Produto produto) {
        Carrinho carrinho = buscarCarrinhoPorCliente(cliente);

        if (carrinho.getProdutos().containsKey(produto)) {
            int quantidadeAtual = carrinho.getProdutos().get(produto);
            if (quantidadeAtual > 1) {
                carrinho.getProdutos().put(produto.getId(), quantidadeAtual - 1);
            } else {
                carrinho.getProdutos().remove(produto);
            }
        }

        carrinhoRepository.save(carrinho);
    }

    public void atualizarQuantidade(Cliente cliente, Produto produto, int quantidade) {
        Carrinho carrinho = buscarCarrinhoPorCliente(cliente);

        if (quantidade > 0) {
            carrinho.getProdutos().put(produto.getId(), quantidade);
        } else {
            carrinho.getProdutos().remove(produto);
        }

        carrinhoRepository.save(carrinho);
    }

    public Map<Long, Integer> listarProdutosComQuantidades(Cliente cliente) {
        Carrinho carrinho = buscarCarrinhoPorCliente(cliente);
        return carrinho.getProdutos();
    }

    public void salvarCarrinho(Carrinho carrinho) {
        carrinhoRepository.save(carrinho);
    }

    public Carrinho getCarrinhoByClienteId(Cliente cliente) {
        return carrinhoRepository.findByClienteId(cliente.getId()).orElse(null);
    }

    public void limparCarrinho(Cliente cliente, HttpSession session) {
        Carrinho carrinho = buscarCarrinhoPorCliente(cliente);

        if (carrinho != null) {
            carrinho.getProdutos().clear();
            carrinho.setTotal(0.0);
            carrinhoRepository.save(carrinho);
        }

        session.removeAttribute("carrinhoId");
    }
}
