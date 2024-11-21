package ecommerce.junior.service;

import ecommerce.junior.dto.ClienteForm;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Endereco;
import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.User;
import ecommerce.junior.repository.ClienteRepository;
import ecommerce.junior.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private ClienteRepository clienteRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User authenticate(String email, String senha) {
        User user = userRepository.findByEmail(email);
        if(user != null && passwordEncoder.matches(senha, user.getSenha())) {
            return user;
        }
        return null;
    }

    public List<User> getUsersByName(String nome) {
        if (nome == null || nome.isEmpty()) {
            return userRepository.findAll();
        } else {
            return userRepository.findByNomeContaining(nome);
        }
    }

    // método que cadastra o usuário (importante!!)
    public void createUser(User user, String senhaConfirmacao) throws IllegalArgumentException {
        if(!isNomeValido(user.getNome())){
            throw new IllegalArgumentException("Este nome nao é valido!");
        }

        if (!user.getSenha().equals(senhaConfirmacao)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        if (userRepository.existsByCpf(user.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        CPFValidator cpfValidator = new CPFValidator();
        try {
            cpfValidator.assertValid(user.getCpf());
        } catch (InvalidStateException e) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        user.setSenha(passwordEncoder.encode(user.getSenha()));
        user.setAtivo(true);

        userRepository.save(user);
    }

    private boolean isEnderecoFaturamentoCompleto(Endereco enderecoFaturamento) {
        return enderecoFaturamento != null &&
                enderecoFaturamento.getCep() != null && !enderecoFaturamento.getCep().isEmpty() &&
                enderecoFaturamento.getLogradouro() != null && !enderecoFaturamento.getLogradouro().isEmpty() &&
                enderecoFaturamento.getNumero() != null && !enderecoFaturamento.getNumero().isEmpty() &&
                enderecoFaturamento.getBairro() != null && !enderecoFaturamento.getBairro().isEmpty() &&
                enderecoFaturamento.getLocalidade() != null && !enderecoFaturamento.getLocalidade().isEmpty() &&
                enderecoFaturamento.getUf() != null && !enderecoFaturamento.getUf().isEmpty();
    }


    private boolean isNomeValido(String nome) {
        String[] palavras = nome.trim().split("\\s+");
        if (palavras.length < 2) {
            return false; // pelo menos duas palavras
        }
        for (String palavra : palavras) {
            if (palavra.length() < 3) {
                return false; // pelo menos 3 letras
            }
        }
        return true; // nome válido
    }

    public void updateUser(User user, String novaSenha, String senhaConfirmacao, HttpSession session) throws Exception {
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            throw new Exception("Usuário não está logado.");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new Exception("Usuário logado não existe"));

        if (!user.getId().equals(currentUser.getId())) {
            throw new Exception("Você não tem permissão para atualizar este usuário.");
        }

        currentUser.setNome(user.getNome());

        if (novaSenha != null && !novaSenha.isEmpty()) {
            if (!novaSenha.equals(senhaConfirmacao)) {
                throw new Exception("A confirmação da senha não corresponde.");
            }
            currentUser.setSenha(novaSenha);
        }

        userRepository.save(currentUser);
    }

    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuário não encontrado."));
    }

    public void alterarStatus(Long id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuário não encontrado."));
        user.setAtivo(!user.isAtivo());
        userRepository.save(user);
    }

    public Grupo getUserRole(Long userId) throws Exception {
        User user = getUserById(userId);
        return user.getTipo();
    }
    

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findUsersByName(String nome) {
        return userRepository.findByNomeContainingIgnoreCase(nome);
    }

    public User findByNome(String nome) {
        return userRepository.findByNome(nome);
    }

    public User findById(Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuário não encontrado."));
    }

    public Long findIdByNome(String nome) throws Exception {
        Optional<Long> userId = userRepository.findIdByNome(nome);
        return userId.orElseThrow(() -> new Exception("Usuário não encontrado pelo nome."));
    }
}
