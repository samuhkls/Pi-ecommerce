package ecommerce.junior.repository;

import ecommerce.junior.model.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagemRepository extends JpaRepository<Imagem, Long> {
    List<Imagem> findByProdutoId(Long produtoId);
    Imagem findFirstByProdutoId(Long produtoId);
}
