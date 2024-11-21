package ecommerce.junior.controller;

import ecommerce.junior.model.Imagem;
import ecommerce.junior.model.Produto;
import ecommerce.junior.service.ImagemService;
import ecommerce.junior.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@RequestMapping("/admin/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ImagemService imagemService;

    @Autowired
    private HttpSession session;

    @GetMapping("/novo")
    public String novoProdutoForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "cadastrar-produto";
    }

    @PostMapping("/salvar")
    public String salvarProduto(
            @RequestParam("nome") String nome,
            @RequestParam("descricaoDetalhada") String descricaoDetalhada,
            @RequestParam("preco") Double preco,
            @RequestParam("quantidadeEmEstoque") Integer quantidadeEmEstoque,
            @RequestParam("ativo") Boolean ativo,
            @RequestParam("imagens") MultipartFile[] imagens) throws IOException {

        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricaoDetalhada(descricaoDetalhada);
        produto.setPreco(preco);
        produto.setQuantidadeEmEstoque(quantidadeEmEstoque);
        produto.setAtivo(ativo);

        List<Imagem> listaImagens = new ArrayList<>();
        String uploadDir = "src/main/resources/static/uploads/"; // Caminho para a pasta de uploads

        for (MultipartFile imagem : imagens) {
            if (!imagem.isEmpty()) {
                // Gerar o nome do arquivo
                String fileName = imagem.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);

                // Salvar a imagem fisicamente no diretório 'uploads'
                Files.copy(imagem.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Salvar a URL relativa no banco de dados
                Imagem novaImagem = new Imagem();
                novaImagem.setUrl("uploads/" + fileName);  // Salvar apenas o caminho relativo
                novaImagem.setProduto(produto); // Associando a imagem ao produto
                listaImagens.add(novaImagem);
            }
        }

        produto.setImagens(listaImagens); // Associando a lista de imagens ao produto
        produtoService.salvarProduto(produto); // Salva o produto com imagens associadas

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

    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Produto produto = produtoService.getProdutoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        model.addAttribute("produto", produto);
        return "editar-produto";
    }


    @PostMapping("/editar/{id}")
    public String atualizarProduto(@PathVariable Long id,
                                   @RequestParam("nome") String nome,
                                   @RequestParam("descricaoDetalhada") String descricaoDetalhada,
                                   @RequestParam("preco") Double preco,
                                   @RequestParam("quantidadeEmEstoque") Integer quantidadeEmEstoque,
                                   @RequestParam("ativo") Boolean ativo,
                                   @RequestParam(value = "imagens", required = false) MultipartFile[] imagens) throws IOException {

        Produto produto = produtoService.getProdutoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        // Atualiza os dados básicos do produto
        produto.setNome(nome);
        produto.setDescricaoDetalhada(descricaoDetalhada);
        produto.setPreco(preco);
        produto.setQuantidadeEmEstoque(quantidadeEmEstoque);
        produto.setAtivo(ativo);

        // Remove as imagens antigas do banco
        List<Imagem> imagensAntigas = produto.getImagens();
        if (imagensAntigas != null && !imagensAntigas.isEmpty()) {
            imagensAntigas.forEach(imagemService::deletarImagem); // Método que remove imagens do banco
        }
        produto.getImagens().clear(); // Remove as referências no objeto Produto

        // Processa e adiciona as novas imagens
        if (imagens != null && imagens.length > 0) {
            String uploadDir = "src/main/resources/static/uploads/"; // Caminho para uploads
            for (MultipartFile imagem : imagens) {
                if (!imagem.isEmpty()) {
                    String fileName = imagem.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + fileName);

                    // Salvar fisicamente no diretório de uploads
                    Files.copy(imagem.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Criar a nova entidade Imagem
                    Imagem novaImagem = new Imagem();
                    novaImagem.setUrl("uploads/" + fileName);
                    novaImagem.setProduto(produto);

                    // Adiciona ao produto
                    produto.getImagens().add(novaImagem);
                }
            }
        }

        // Salva o produto atualizado no banco
        produtoService.salvarProduto(produto);

        return "redirect:/admin/produtos";
    }


    @GetMapping("/detalhes/{id}")
    public String detalhesProduto(@PathVariable Long id, Model model) {
        Optional<Produto> produto = produtoService.getProdutoById(id);
        if (produto.isPresent()) {
            // Busca as imagens relacionadas ao produto
            List<Imagem> imagens = imagemService.getImagensByProdutoId(id); // Método que busca imagens pelo id do produto
            model.addAttribute("produto", produto.get());
            model.addAttribute("imagens", imagens);
            return "detalhe-produto"; // Sua página de detalhes do produto
        } else {
            return "redirect:/home"; // Caso o produto não seja encontrado
        }
    }


    @GetMapping("/home")
    public String exibirPaginaPrincipal(Model model) {
        Pageable pageable = PageRequest.of(0, 6, Sort.by("id").descending());
        Page<Produto> produtos = produtoService.getAllProdutos(pageable);

        // Mapeamento para associar uma única imagem por produto
        Map<Long, Imagem> produtoImagemMap = new HashMap<>();
        for (Produto produto : produtos) {
            // Aqui você chama o método que retorna uma imagem associada ao produto
            Imagem imagem = imagemService.getImagemByProdutoId(produto.getId());
            produtoImagemMap.put(produto.getId(), imagem);
        }

        model.addAttribute("produtoImagemMap", produtoImagemMap);
        model.addAttribute("produtos", produtos);
        return "home";
    }



    // Método de pagamento
    @GetMapping("/pedidos")
    public String pagamento(Model model) {
        return "pedido";
    }
}
