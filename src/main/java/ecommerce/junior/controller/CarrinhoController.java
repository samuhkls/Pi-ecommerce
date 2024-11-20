package ecommerce.junior.controller;

import ecommerce.junior.model.Carrinho;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Produto;
import ecommerce.junior.service.CarrinhoService;
import ecommerce.junior.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Controller
@SessionAttributes("carrinho")
public class CarrinhoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private CarrinhoService carrinhoService;


    @ModelAttribute("carrinho")
    public Carrinho inicializarCarrinho(HttpSession session) {
        Cliente clienteLogado = (Cliente) session.getAttribute("cliente");
        Carrinho carrinho = carrinhoService.buscarCarrinhoPorCliente(clienteLogado);
        return carrinho;
    }



    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionarProdutoAoCarrinho(@PathVariable Long id,
                                             @ModelAttribute("carrinho") Carrinho carrinho,
                                             HttpSession session,
                                             Model model) {
        Optional<Produto> produtoOpt = produtoService.getProdutoById(id);

        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            Cliente clienteLogado = (Cliente) session.getAttribute("cliente");
            carrinhoService.adicionarProduto(clienteLogado, produto);
        }

        model.addAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho")
    public String visualizarCarrinho(@ModelAttribute("carrinho") Carrinho carrinho, Model model) {
        model.addAttribute("carrinho", carrinho.getProdutos()); // Passa apenas o mapa de produtos
        double total = carrinho.getProdutos()
                .entrySet()
                .stream()
                .mapToDouble(entry -> entry.getKey().getPreco() * entry.getValue()) // Preço * Quantidade
                .sum();
        model.addAttribute("total", total);
        return "carrinho";
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

    @GetMapping("/carrinho/pagamento")
    public String pagamento(@ModelAttribute("carrinho") Carrinho carrinho, Model model) {
        double total = carrinho.getProdutos()
                .entrySet()
                .stream()
                .mapToDouble(entry -> entry.getKey().getPreco() * entry.getValue()) // Preço * Quantidade
                .sum();
        model.addAttribute("total", total);
        return "pagamento";
    }


}
