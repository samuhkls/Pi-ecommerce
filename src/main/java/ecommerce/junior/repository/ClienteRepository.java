package ecommerce.junior.repository;

import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByEmail(String email);
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByCpf(String cpf);
}

