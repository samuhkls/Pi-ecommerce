package ecommerce.junior.controller;
import ecommerce.junior.model.User;
import ecommerce.junior.service.ProdutoService;
import ecommerce.junior.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;


@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping("/cadastrar")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("produto", new Produto());
        return "cadastrar-produto"; // Nome do template HTML
    }

    @PostMapping("/cadastrar")
    public String cadastrarProduto(@ModelAttribute Produto produto, Model model) {
        try {
            produtoService.salvarProduto(produto);
            return "redirect:/produtos"; // Redireciona para a lista de produtos
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Erro ao cadastrar o produto.");
            return "cadastrar-produto";
        }
    }
}
