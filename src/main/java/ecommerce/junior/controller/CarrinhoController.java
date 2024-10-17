package ecommerce.junior.controller;

import ecommerce.junior.model.CarrinhoItem;
import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.CarrinhoItemRepository;
import ecommerce.junior.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@SessionAttributes("carrinho")
public class CarrinhoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private CarrinhoItemRepository carrinhoItemRepository;

    @ModelAttribute("carrinho")
    public List<CarrinhoItem> inicializarCarrinho() {
        return new ArrayList<>();
    }


    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionarProdutoAoCarrinho(@PathVariable Long id, @ModelAttribute("carrinho") List<CarrinhoItem> carrinho, Model model) {
        Optional<Produto> produtoOpt = produtoService.getProdutoById(id);

        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            boolean produtoJaNoCarrinho = false;

            for (CarrinhoItem item : carrinho) {
                if (item.getProduto().getId().equals(produto.getId())) {
                    item.incrementarQuantidade();
                    produtoJaNoCarrinho = true;
                    break;
                }
            }

            if (!produtoJaNoCarrinho) {
                carrinho.add(new CarrinhoItem(produto, 1));
            }

            for (CarrinhoItem item : carrinho) {
                carrinhoItemRepository.save(item);
            }
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

}
