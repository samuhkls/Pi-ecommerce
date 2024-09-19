package ecommerce.junior.controller;

import ecommerce.junior.model.Produto;
import ecommerce.junior.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listarProdutos(Model model) {
        List<Produto> produtos = produtoService.getAllProdutos();
        model.addAttribute("produtos", produtos);
        return "lista_produtos"; // Nome do template Thymeleaf
    }

    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        Produto produto = new Produto();
        model.addAttribute("produto", produto);
        System.out.println("Produto adicionado ao modelo: " + produto);  // Log para depuração
        return "form_produto";
    }


    @PostMapping("/novo")
    public String cadastrarProduto(@Valid @ModelAttribute Produto produto, BindingResult result,
                                   @RequestParam("imagens") MultipartFile[] imagens, Model model) throws IOException {
        if (result.hasErrors()) {
            return "form_produto"; // Se houver erros, retorna ao formulário
        }
        produtoService.salvarProdutoComImagens(produto, imagens); // Salva o produto com as imagens
        return "redirect:/admin/produtos"; // Redireciona para a lista de produtos após o sucesso
    }
    @PostMapping("/status")
    public String alterarStatusProduto(@RequestParam("id") Long id) {
        produtoService.alterarStatus(id);
        return "redirect:/admin/produtos";
    }
}
