package ecommerce.junior.service;

import ecommerce.junior.model.User;
import ecommerce.junior.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createUser(User user, String senhaConfirmacao) throws Exception {

        validarSenhas(user.getSenha(), senhaConfirmacao);
        validarEmailUnico(user.getEmail());
        validarCpf(user.getCpf());

        user.setSenha(passwordEncoder.encode(user.getSenha()));
        user.setAtivo(true);
        userRepository.save(user);
    }

    public void updateUser(User user, String senhaConfirmacao, User loggedInUser) throws Exception {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new Exception("Usuário não encontrado."));

        if (!existingUser.getEmail().equals(user.getEmail())) {
            throw new Exception("Não é permitido alterar o email.");
        }

        validarSenhas(user.getSenha(), senhaConfirmacao);
        validarCpf(user.getCpf());

        existingUser.setNome(user.getNome());
        existingUser.setCpf(user.getCpf());
        existingUser.setSenha(passwordEncoder.encode(user.getSenha()));

        if (!loggedInUser.getId().equals(user.getId())) {
            existingUser.setTipo(user.getTipo());
        }

        userRepository.save(existingUser);
    }

    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuário não encontrado."));
    }

    private void validarSenhas(String senha, String senhaConfirmacao){
        if(!senha.equals(senhaConfirmacao)){
            throw new IllegalArgumentException("As senhas não coincidem");
        }
    }

    private void validarEmailUnico(String email){
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Email já cadastrado");
        }
    }

    private void validarCpf(String cpf) throws  IllegalArgumentException {
        CPFValidator cpfValidator = new CPFValidator();
        try{
            cpfValidator.assertValid(cpf);
        } catch (InvalidStateException e) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }
}
