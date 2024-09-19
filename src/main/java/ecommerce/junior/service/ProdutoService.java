package ecommerce.junior.service;

import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Page<Produto> getAllProdutos(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public Page<Produto> buscarPorNomeParcial(String nome, Pageable pageable) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Optional<Produto> getProdutoById(Long id) {
        return produtoRepository.findById(id);
    }

    @Transactional
    public void alterarStatus(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));
        produto.setAtivo(!produto.isAtivo());  // Alterna o status ativo/inativo
        produtoRepository.save(produto);
    }
    public Produto getProdutoByIdNoOptional(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));
    }


}
