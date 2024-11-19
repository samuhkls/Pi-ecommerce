package ecommerce.junior.repository;

import ecommerce.junior.model.Carrinho;
import ecommerce.junior.model.CarrinhoItem;
import ecommerce.junior.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByClienteId(Long clienteId);

}
