package ecommerce.junior.service;

import ecommerce.junior.model.User;
import ecommerce.junior.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getUsersByName(String nome) {
        if (nome == null || nome.isEmpty()) {
            return userRepository.findAll();
        } else {
            return userRepository.findByNomeContaining(nome);
        }
    }

    public void createUser(User user, String senhaConfirmacao) throws IllegalArgumentException {
        if (!user.getSenha().equals(senhaConfirmacao)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        // Valida o CPF
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

    public void updateUser(User user, String senhaConfirmacao, User loggedInUser) throws Exception {
        if (!user.getId().equals(loggedInUser.getId())) {
            throw new SecurityException("Você não tem permissão para atualizar este usuário.");
        }

        if (senhaConfirmacao != null && !senhaConfirmacao.isEmpty()) {
            if (!passwordEncoder.matches(senhaConfirmacao, loggedInUser.getSenha())) {
                throw new Exception("Senha de confirmação incorreta.");
            }
            if (user.getSenha() != null && !user.getSenha().isEmpty()) {
                user.setSenha(passwordEncoder.encode(user.getSenha())); // Codifica a nova senha
            } else {
                user.setSenha(loggedInUser.getSenha());
            }
        } else {
            user.setSenha(loggedInUser.getSenha());
        }

        // Atualiza o usuário no repositório
        userRepository.save(user);
    }

    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuário não encontrado."));
    }

    public void alterarStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setAtivo(!user.isAtivo());
        userRepository.save(user);
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