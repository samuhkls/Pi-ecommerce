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
    public List<CarrinhoItem> inicializarCarrinho(@SessionAttribute(value = "carrinho", required = false) List<CarrinhoItem> carrinhoExistente) {
        return carrinhoExistente != null ? carrinhoExistente : new ArrayList<>();
    }

    @ModelAttribute("enderecoEntrega")
    public String inicializarEnderecoEntrega(@SessionAttribute(value = "enderecoEntrega", required = false) String enderecoExistente) {
        return enderecoExistente != null ? enderecoExistente : "";
    }


    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionarProdutoAoCarrinho(@PathVariable Long id, @ModelAttribute("carrinho") List<CarrinhoItem> carrinho, Model model) {
        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho")
    public String visualizarCarrinho(@ModelAttribute("carrinho") List<CarrinhoItem> carrinho, Model model) {
        double total = carrinho.stream()
                .mapToDouble(item -> item.getQuantidade() * item.getProduto().getPreco())
                .sum();
        model.addAttribute("total", total);
        return "carrinho";
    }

    @PostMapping("/carrinho/atualizarEndereco")
    public String atualizarEndereco(@RequestParam("endereco") String endereco, Model model) {
        if (endereco == null || endereco.isBlank()) {
            model.addAttribute("erroEndereco", "O endereço de entrega é obrigatório para prosseguir.");
            return "carrinho";
        }
        model.addAttribute("enderecoEntrega", endereco);
        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho/pagamento")
    public String pagamento(Model model) {
        return "pagamento";
    }
}
