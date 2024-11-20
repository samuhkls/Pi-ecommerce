package ecommerce.junior.service;

import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.User;
import ecommerce.junior.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Cliente authenticate(String email, String senha) {
        Cliente cliente = clienteRepository.findByEmail(email);
        // if(cliente != null && passwordEncoder.matches(senha, cliente.getSenha())) {
        if (cliente != null && cliente.getSenha().equals(senha)) {
            return cliente;
        }
        return null;
    }


    public Cliente getClienteById(Long clienteId) {
        return clienteRepository.findById(clienteId).orElse(null);
    }

}
