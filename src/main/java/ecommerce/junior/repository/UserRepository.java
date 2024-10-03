package ecommerce.junior.repository;
import java.util.List;
import java.util.Optional;

import ecommerce.junior.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    List<User> findByNomeContaining(String nome);
    List<User> findByNomeContainingIgnoreCase(String nome);
    User findByNome(String nome);
    Optional<User> findById(Long id);
    Optional<Long> findIdByNome(String nome);
    User findByEmail(String email);

}
