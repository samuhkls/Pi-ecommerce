package ecommerce.junior.controller;

import ecommerce.junior.model.Produto;
import ecommerce.junior.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/admin/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping("/novo")
    public String novoProdutoForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "cadastrar-produto";
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
            produto.setImagem(imagem.getBytes());
        }

        produtoService.salvarProduto(produto);
        return "redirect:/admin/produtos";
    }

    @GetMapping
    public String listarProdutos(@RequestParam(value = "nome", required = false) String nome,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 Model model) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").descending());

        Page<Produto> produtos;
        if (nome != null && !nome.isEmpty()) {
            produtos = produtoService.buscarPorNomeParcial(nome, pageable);
        } else {
            produtos = produtoService.getAllProdutos(pageable);
        }

        model.addAttribute("produtos", produtos);
        model.addAttribute("nome", nome);
        return "lista-produtos";
    }

    @PostMapping("/alterar-status/{id}")
    public String alterarStatus(@PathVariable Long id) {
        produtoService.alterarStatus(id);
        return "redirect:/admin/produtos";
    }

    @GetMapping("/visualizar/{id}")
    public String visualizarProduto(@PathVariable Long id, Model model) {
        Optional<Produto> produto = produtoService.getProdutoById(id);
        if (produto.isPresent()) {
            model.addAttribute("produto", produto.get());
            return "visualizar-produto";
        } else {
            return "redirect:/admin/produtos";
        }
    }

    // Exibe o formulário para editar um produto existente
    @GetMapping("/alterar/{id}")
    public String alterarProdutoForm(@PathVariable Long id, Model model) {
        Optional<Produto> produto = produtoService.getProdutoById(id);
        if (produto.isPresent()) {
            model.addAttribute("produto", produto.get());
            return "cadastrar-produto";
        } else {
            return "redirect:/admin/produtos";
        }
    }

    // Atualiza um produto existente
    @PostMapping("/atualizar/{id}")
    public String atualizarProduto(@PathVariable Long id,
                                   @RequestParam("nome") String nome,
                                   @RequestParam("descricaoDetalhada") String descricaoDetalhada,
                                   @RequestParam("preco") Double preco,
                                   @RequestParam("quantidadeEmEstoque") Integer quantidadeEmEstoque,
                                   @RequestParam("ativo") Boolean ativo,
                                   @RequestParam("imagem") MultipartFile imagem) throws IOException {
        Produto produto = produtoService.getProdutoById(id).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        produto.setNome(nome);
        produto.setDescricaoDetalhada(descricaoDetalhada);
        produto.setPreco(preco);
        produto.setQuantidadeEmEstoque(quantidadeEmEstoque);
        produto.setAtivo(ativo);

        if (!imagem.isEmpty()) {
            produto.setImagem(imagem.getBytes());
        }

        produtoService.salvarProduto(produto);
        return "redirect:/admin/produtos";
    }
}
