package ecommerce.junior.service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void salvarProduto(Produto produto) {
        // Validar os dados do produto antes de salvar, se necessário
        if (produto.getAvaliacao() < 1 || produto.getAvaliacao() > 5) {
            throw new IllegalArgumentException("A avaliação deve estar entre 1 e 5.");
        }

        // Salvando o produto usando EntityManager
        if (produto.getId() == null) {
            entityManager.persist(produto); // Salva o produto se for novo
        } else {
            entityManager.merge(produto); // Atualiza o produto se já existir
        }
    }
}