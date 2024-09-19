package ecommerce.junior.service;

import ecommerce.junior.model.Imagem;
import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.ImagemRepository;
import ecommerce.junior.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ImagemRepository imagemRepository;

    public Page<Produto> buscarTodosPaginado(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        return produto.orElseThrow(() -> new RuntimeException("Produto não encontrado!"));
    }

    public void salvarProdutoComImagens(Produto produto, MultipartFile[] imagens) throws IOException {
        produto = produtoRepository.save(produto);

        for (MultipartFile imagemFile : imagens) {
            if (!imagemFile.isEmpty()) {
                String novoNome = UUID.randomUUID().toString() + "_" + imagemFile.getOriginalFilename();
                Path caminho = Paths.get("diretorio/imagens").resolve(novoNome);
                Files.copy(imagemFile.getInputStream(), caminho);

                Imagem imagem = new Imagem();
                imagem.setUrl(novoNome);
                imagem.setProduto(produto);
                imagemRepository.save(imagem);
            }
        }
    }

    public void alterarStatus(Long id) {
        Produto produto = buscarPorId(id);
        produto.setAtivo(!produto.isAtivo());
        produtoRepository.save(produto);
    }

    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }
}
