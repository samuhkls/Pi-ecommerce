package ecommerce.junior.controller;

import ecommerce.junior.model.Carrinho;
import ecommerce.junior.model.CarrinhoItem;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.CarrinhoItemRepository;
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

    @Autowired
    private CarrinhoItemRepository carrinhoItemRepository;

    @ModelAttribute("carrinho")
    public Optional<Carrinho> inicializarCarrinho(HttpSession session) {
        Cliente clienteLogado = (Cliente) session.getAttribute("cliente");
        Carrinho carrinho = carrinhoService.buscarCarrinhoPorCliente(clienteLogado);
        return Optional.ofNullable(carrinho);
    }



    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionarProdutoAoCarrinho(@PathVariable Long id,
                                             @ModelAttribute("carrinho") List<CarrinhoItem> carrinho,
                                             HttpSession session,
                                             Model model) {
        Optional<Produto> produtoOpt = produtoService.getProdutoById(id);

        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            Cliente clienteLogado = (Cliente) session.getAttribute("cliente");
            carrinhoService.adicionarProduto(clienteLogado, produto, (Carrinho) carrinho);
        }

        model.addAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho")
    public String visualizarCarrinho(@ModelAttribute("carrinho") List<CarrinhoItem> carrinho, Model model) {

        if (carrinho == null || carrinho.isEmpty()) {
            model.addAttribute("total", 0.0);
            return "carrinho";
        }

        double total = carrinho.stream()
                .mapToDouble(item -> item.getQuantidade() * item.getProduto().getPreco())
                .sum();

        model.addAttribute("total", total);

        System.out.println(carrinho);
        model.addAttribute("carrinho", carrinho);
        return "carrinho";
    }

    @PostMapping("/carrinho/atualizar/{id}")
    public String atualizarQuantidade(@PathVariable Long id, @RequestParam Integer quantidade, @ModelAttribute("carrinho") List<CarrinhoItem> carrinho) {
        for (CarrinhoItem item : carrinho) {
            if (item.getProduto().getId().equals(id)) {
                item.setQuantidade(quantidade);
                carrinhoItemRepository.save(item);
                break;
            }
        }
        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho/pagamento")
    public String pagamento(Model model) {
        return "pagamento";
    }

}
