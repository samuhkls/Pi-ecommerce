package ecommerce.junior.controller;

import ecommerce.junior.model.Carrinho;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.CarrinhoRepository;
import ecommerce.junior.service.CarrinhoService;
import ecommerce.junior.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("carrinho")
public class CarrinhoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private CarrinhoRepository carrinhoRepository;


    @ModelAttribute("carrinho")
    public Carrinho inicializarCarrinho(HttpSession session) {
        return carrinhoService.obterOuCriarCarrinho(session);
    }



    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionarProdutoAoCarrinho(@PathVariable Long id,
                                             HttpSession session,
                                             Model model) {
        // Obtenha ou crie o carrinho na sessão
        Carrinho carrinho = carrinhoService.obterOuCriarCarrinho(session);

        Optional<Produto> produtoOpt = produtoService.getProdutoById(id);

        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            carrinho.adicionarProduto(produto.getId(), 1); // Adiciona 1 unidade como padrão
            carrinhoRepository.save(carrinho); // Salva as alterações no banco de dados
        }

        model.addAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }


    @GetMapping("/carrinho")
    public String visualizarCarrinho(HttpSession session, Model model) {
        // Recupera ou cria o carrinho da sessão
        Carrinho carrinho = carrinhoService.obterOuCriarCarrinho(session);

        // Recupera os IDs e quantidades do carrinho
        Map<Long, Integer> produtosCarrinho = carrinho.getProdutos(); // IDs + Quantidades

        // Busca os produtos pelo ID
        List<Produto> produtos = produtoService.getProdutosByIds(produtosCarrinho.keySet());

        // Constrói o mapa de ID -> Produto para fácil acesso no template
        Map<Long, Produto> produtosPorId = produtos.stream()
                .collect(Collectors.toMap(Produto::getId, produto -> produto));

        // Calcula o total do carrinho
        double total = calcularTotal(produtosCarrinho, produtosPorId);

        // Adiciona ao modelo
        model.addAttribute("carrinho", produtosCarrinho); // IDs e quantidades
        model.addAttribute("produtos", produtosPorId);   // IDs e produtos
        model.addAttribute("total", total);

        return "carrinho";
    }

    private double calcularTotal(Map<Long, Integer> produtosCarrinho, Map<Long, Produto> produtosPorId) {
        return produtosCarrinho.entrySet()
                .stream()
                .mapToDouble(entry -> produtosPorId.get(entry.getKey()).getPreco() * entry.getValue())
                .sum();
    }


    @PostMapping("/carrinho/atualizar/{id}")
    public String atualizarQuantidade(@PathVariable Long id,
                                      @RequestParam Integer quantidade,
                                      @ModelAttribute("carrinho") Carrinho carrinho) {
        Produto produto = produtoService.getProdutoById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        carrinho.atualizarQuantidade(produto, quantidade);
        carrinhoService.salvarCarrinho(carrinho);
        return "redirect:/carrinho";
    }

//    @GetMapping("/carrinho/pagamento")
//    public String pagamento(@ModelAttribute("carrinho") Carrinho carrinho, Model model) {
//        double total = carrinho.getProdutos()
//                .entrySet()
//                .stream()
//                .mapToDouble(entry -> entry.getKey().getPreco() * entry.getValue()) // Preço * Quantidade
//                .sum();
//        model.addAttribute("total", total);
//        return "pagamento";
//    }


}
