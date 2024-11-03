package ecommerce.junior.service;

import ecommerce.junior.model.Endereco;
import ecommerce.junior.model.User;
import ecommerce.junior.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import org.springframework.web.client.RestTemplate;

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

    // método que cadastra o usuário (importante!!)
    public void createUser(User user, String senhaConfirmacao) throws IllegalArgumentException {
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

        if (!EnderecoService.validarCep(user.getEnderecoFaturamento().getCep())) {
            throw new IllegalArgumentException("CEP inválido.");
        }

        if (!isEnderecoFaturamentoCompleto(user.getEnderecoFaturamento())) { // se nao for true, o cep é invalido
            throw new IllegalArgumentException("Endereço de faturamento incompleto.");
        }

        user.setSenha(passwordEncoder.encode(user.getSenha()));
        user.setAtivo(true);

        userRepository.save(user);
    }


    // Método que verifica se TODOS os campos do endereco estão completos para fazer o cadastro
    private boolean isEnderecoFaturamentoCompleto(Endereco enderecoFaturamento) {
        return enderecoFaturamento != null &&
                enderecoFaturamento.getCep() != null && !enderecoFaturamento.getCep().isEmpty() &&
                enderecoFaturamento.getLogradouro() != null && !enderecoFaturamento.getLogradouro().isEmpty() &&
                enderecoFaturamento.getNumero() != null && !enderecoFaturamento.getNumero().isEmpty() &&
                enderecoFaturamento.getBairro() != null && !enderecoFaturamento.getBairro().isEmpty() &&
                enderecoFaturamento.getLocalidade() != null && !enderecoFaturamento.getLocalidade().isEmpty() &&
                enderecoFaturamento.getUf() != null && !enderecoFaturamento.getUf().isEmpty();
    }

    // Método que verifica se o cep fornecido pelo usuario é valido
//    private boolean isCepValido(String cep) {
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "https://viacep.com.br/ws/" + cep + "/json/";
//
//        try {
//            ResponseEntity<Endereco> response = restTemplate.getForEntity(url, Endereco.class);
//            return response.getStatusCode().is2xxSuccessful() && response.getBody() != null;
//        } catch (Exception e) {
//            return false; // sera falso se ocorrer um erro na requisição
//        }
//    }

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
