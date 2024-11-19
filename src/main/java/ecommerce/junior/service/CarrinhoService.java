package ecommerce.junior.service;

import ecommerce.junior.model.Carrinho;
import ecommerce.junior.model.CarrinhoItem;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.CarrinhoItemRepository;
import ecommerce.junior.repository.CarrinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoItemRepository carrinhoItemRepository;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private CarrinhoService carrinhoService;

    public Carrinho buscarCarrinhoPorCliente(Cliente cliente) {
        if (cliente == null) {
            return new Carrinho(); // Retorna um carrinho vazio
        }

        List<CarrinhoItem> itens = carrinhoItemRepository.findByCliente(cliente);

        Carrinho carrinho = new Carrinho();
        carrinho.setItens(itens);

        return carrinho;
    }


    public void adicionarProduto(Cliente cliente, Produto produto, Carrinho carrinho) {
        if (produto == null) {
            return;
        }

        boolean produtoJaNoCarrinho = false;

//        Carrinho carrinhoAtual = carrinhoService.buscarCarrinhoPorCliente(usuarioId);
//        CarrinhoItem novoItem = new CarrinhoItem();
//        novoItem.setProduto(produto);
//        novoItem.setQuantidade(quantidade);
//        novoItem.setCarrinho(carrinhoAtual);
//
//        carrinhoAtual.getItens().add(novoItem);
//        carrinhoRepository.save(carrinhoAtual);
    }

    public void atualizarQuantidade(CarrinhoItem item, int quantidade) {
        item.setQuantidade(quantidade);
        carrinhoItemRepository.save(item);
    }

    public void salvarCarrinho(Carrinho carrinho) {
        carrinhoRepository.save(carrinho);
    }

    public Carrinho getCarrinhoByClienteId(Cliente cliente) {
        Carrinho carrinho = carrinhoRepository.findByClienteId(cliente.getId()).orElse(null);
        return carrinho;
    }
}
