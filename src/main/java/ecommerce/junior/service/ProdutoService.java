package ecommerce.junior.service;

import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }

    public void alterarStatus(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));
        produto.setAtivo(!produto.isAtivo());  // Agora, o método isAtivo() será reconhecido
        produtoRepository.save(produto);
    }

}
