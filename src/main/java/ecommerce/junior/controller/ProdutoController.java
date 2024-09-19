package ecommerce.junior.controller;


import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.ProdutoRepository;
import ecommerce.junior.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;


    @GetMapping("/novo")
    public String novoProdutoForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "cadastrar-produto"; // Nome do arquivo HTML para cadastro de produtos
    }

    @GetMapping
    public String listarProdutos(Model model) {
        List<Produto> produtos = produtoService.getAllProdutos();
        model.addAttribute("produtos", produtos);
        return "lista-produtos"; // Nome do template HTML
    }

    @PostMapping("/salvar")
    public String salvarProduto(@RequestParam("nome") String nome,
                                @RequestParam("descricaoDetalhada") String descricaoDetalhada,
                                @RequestParam("preco") Double preco,
                                @RequestParam("quantidadeEmEstoque") Integer quantidadeEmEstoque,
                                @RequestParam("ativo") Boolean ativo,
                                @RequestParam("imagem") MultipartFile imagem) throws IOException {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricaoDetalhada(descricaoDetalhada);
        produto.setPreco(preco);
        produto.setQuantidadeEmEstoque(quantidadeEmEstoque);
        produto.setAtivo(ativo);

        if (!imagem.isEmpty()) {
            produto.setImagem(imagem.getBytes()); // Converte a imagem para bytes e salva no banco
        }

        produtoService.salvarProduto(produto);
        return "redirect:/admin/produtos"; // Redireciona para a lista de produtos
    }

    @PostMapping("/alterar-status/{id}")
    public String alterarStatus(@PathVariable Long id) {
        produtoService.alterarStatus(id);
        return "redirect:/admin/produtos"; // Redireciona para a lista de produtos
    }
}
