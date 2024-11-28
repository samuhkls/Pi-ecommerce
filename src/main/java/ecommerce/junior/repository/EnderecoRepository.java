package ecommerce.junior.repository;

import ecommerce.junior.model.Endereco;
import ecommerce.junior.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    // Buscar todos os endereços de um cliente específico
    List<Endereco> findByCliente(Cliente cliente);

    // Buscar endereço por CEP
    public List<Endereco> findByCep(String cep);

    // Buscar endereço por bairro
    public List<Endereco> findByBairro(String bairro);

    // Buscar um endereço por ID (Já existe por padrão em JpaRepository)
    Optional<Endereco> findById(Long id);
}
