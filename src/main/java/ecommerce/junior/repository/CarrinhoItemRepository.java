package ecommerce.junior.repository;

import ecommerce.junior.model.CarrinhoItem;
import ecommerce.junior.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarrinhoItemRepository extends JpaRepository<CarrinhoItem, Long> {
    List<CarrinhoItem> findByCliente(Cliente cliente);
}
