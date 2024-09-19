package ecommerce.junior.service;

import ecommerce.junior.model.User;
import ecommerce.junior.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
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

    public void createUser(User user, String senhaConfirmacao) throws IllegalArgumentException {
        if (!user.getSenha().equals(senhaConfirmacao)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
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

    public void updateUser(User user, HttpSession session) throws Exception {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null){
            throw new Exception("você não tá logado meu mano");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new Exception("Usúario logado não existe"));

        if (!user.getId().equals(currentUser.getId())){
            throw new Exception("Você não tem permissão para atualizar este usuario");
        }

        user.setTipo(currentUser.getTipo());
        user.setCpf(currentUser.getCpf());

        userRepository.save(user);
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
