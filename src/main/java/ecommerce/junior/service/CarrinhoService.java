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

    @Autowired
    private ClienteService clienteService;

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
            if (carrinho != null) {
                return carrinho; // Retorna o carrinho encontrado
            }
        }

        // Criar um carrinho anônimo se nenhum existir
        carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho);
        session.setAttribute("carrinhoId", carrinho.getId());

        // Salva o ID do carrinho na sessão
        session.setAttribute("carrinhoId", carrinho.getId());

        return carrinho;
    }

    public void associarCarrinhoAoCliente(Carrinho carrinho, Cliente cliente) {
        if (carrinho != null && carrinho.getCliente() == null) {
            // Associa o cliente ao carrinho
            carrinho.setCliente(cliente);

            // Salva o carrinho atualizado no repositório
            carrinhoRepository.save(carrinho);
        }
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

    public Carrinho obterOuCriarCarrinho() {
        Carrinho carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho); // Persistir no banco
        return carrinho;
    }

    public Carrinho obterCarrinhoPorId(Long id) {
        System.out.println("Procurando carrinho com ID: " + id); // Log para depuração
        return carrinhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado!"));
    }
}
