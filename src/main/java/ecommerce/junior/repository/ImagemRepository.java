package ecommerce.junior.repository;

import ecommerce.junior.model.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, Long> {

    @Modifying
    @Query("UPDATE Imagem i SET i.principal = ?1 WHERE i.produto.id = ?2")
    void updatePrincipalStatus(Boolean principal, Long produtoId);
}
