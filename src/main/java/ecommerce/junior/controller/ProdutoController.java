package ecommerce.junior.controller;

import ecommerce.junior.model.Imagem;
import ecommerce.junior.model.Produto;
import ecommerce.junior.service.ImagemService;
import ecommerce.junior.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ImagemService imagemService;

    @GetMapping
    public String listar(Model model) {
        try {
            List<Produto> produtos = produtoService.getAllProdutos();
            model.addAttribute("produtos", produtos);
            return "listar-produtos"; // Corrigido para o template correto
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao listar produtos: " + e.getMessage());
            return "erro"; // Assumindo que você tem uma página de erro genérica
        }
    }

    @GetMapping("/novo")
    public String criarProdutoForm(Model model) {
        try {
            model.addAttribute("produto", new Produto());
            return "novo-produto"; // Corrigido para o template correto
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao carregar o formulário de novo produto: " + e.getMessage());
            return "erro"; // Assumindo que você tem uma página de erro genérica
        }
    }
    @PostMapping("/novo")
    public String salvarProduto(
            @ModelAttribute Produto produto,
            @RequestParam("imagemPrincipal") MultipartFile imagemPrincipal,
            @RequestParam("imagens") List<MultipartFile> imagens,
            Model model) {
        try {
            // Salvar o produto
            Produto savedProduto = produtoService.saveProduto(produto);

            // Salvar as imagens adicionais
            for (MultipartFile imagemFile : imagens) {
                if (!imagemFile.isEmpty()) {
                    imagemService.saveImagem(imagemFile, savedProduto.getId());
                }
            }

            // Salvar a imagem principal
            if (!imagemPrincipal.isEmpty()) {
                Imagem imagemPrincipalSalva = imagemService.saveImagem(imagemPrincipal, savedProduto.getId());
                imagemService.definirImagemPrincipal(savedProduto.getId(), imagemPrincipalSalva.getId());
            }

            return "redirect:/produtos"; // Redireciona para a lista de produtos após salvar
        } catch (IOException e) {
            model.addAttribute("error", "Erro ao salvar o produto ou as imagens: " + e.getMessage());
            return "novo-produto";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao salvar o produto: " + e.getMessage());
            return "novo-produto";
        }
    }



    @GetMapping("/{id}")
    public String exibirProduto(@PathVariable("id") Long id, Model model) {
        try {
            Produto produto = produtoService.getProdutoById(id);
            model.addAttribute("produto", produto);
            return "produto-detalhes"; // Corrigido para o template correto
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao exibir o produto: " + e.getMessage());
            return "erro"; // Assumindo que você tem uma página de erro genérica
        }
    }

    @GetMapping("/{id}/editar")
    public String editarProdutoForm(@PathVariable("id") Long id, Model model) {
        try {
            Produto produto = produtoService.getProdutoById(id);
            model.addAttribute("produto", produto);
            return "produto-form"; // Corrigido para o template correto
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao carregar o formulário de edição: " + e.getMessage());
            return "erro"; // Assumindo que você tem uma página de erro genérica
        }
    }

    @PostMapping("/{id}/editar")
    public String atualizarProduto(@PathVariable("id") Long id, @ModelAttribute Produto produto, @RequestParam("imagemPrincipal") MultipartFile imagemPrincipal, @RequestParam("imagens") List<MultipartFile> imagens, Model model) {
        try {
            produto.setId(id);
            Produto updatedProduto = produtoService.saveProduto(produto);

            for (MultipartFile imagemFile : imagens) {
                imagemService.saveImagem(imagemFile, updatedProduto.getId());
            }

            if (!imagemPrincipal.isEmpty()) {
                Imagem imagemPrincipalSalva = imagemService.saveImagem(imagemPrincipal, updatedProduto.getId());
                imagemService.definirImagemPrincipal(updatedProduto.getId(), imagemPrincipalSalva.getId());
            }

            return "redirect:/produtos"; // Redireciona para a lista de produtos após atualizar
        } catch (IOException e) {
            model.addAttribute("error", "Erro ao atualizar o produto ou as imagens: " + e.getMessage());
            return "produto-form"; // Retorna para o formulário de edição com mensagem de erro
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao atualizar o produto: " + e.getMessage());
            return "produto-form"; // Retorna para o formulário de edição com mensagem de erro
        }
    }
}
