package ecommerce.junior.service;

import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }

    public Produto getProdutoById(Long id) throws Exception {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new Exception("Produto não encontrado."));
    }

    public Produto saveProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public void deleteProduto(Long id) throws Exception {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new Exception("Produto não encontrado."));
        produtoRepository.delete(produto);
    }
}
