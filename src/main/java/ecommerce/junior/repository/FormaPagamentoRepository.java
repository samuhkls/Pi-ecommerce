package ecommerce.junior.repository;

import ecommerce.junior.model.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {
    Optional<FormaPagamento> findByPedidoId(Long pedidoId);
}
