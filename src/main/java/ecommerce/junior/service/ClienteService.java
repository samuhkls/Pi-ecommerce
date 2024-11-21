package ecommerce.junior.service;

import ecommerce.junior.dto.ClienteForm;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Endereco;
import ecommerce.junior.model.User;
import ecommerce.junior.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Cliente fromDTO(ClienteForm clienteForm) {
        Cliente cliente = new Cliente();
        cliente.setNome(clienteForm.getNome());
        cliente.setEmail(clienteForm.getEmail());
        cliente.setCpf(clienteForm.getCpf());
        cliente.setSenha(clienteForm.getSenha());

        // Configura endereço de faturamento
        Endereco enderecoFaturamento = clienteForm.getEnderecoFaturamento();
        if (enderecoFaturamento != null) {
            enderecoFaturamento.setCliente(cliente); // Configura a relação bidirecional
            cliente.setEnderecoFaturamento(enderecoFaturamento);
        }

        // Configura endereços de entrega
        List<Endereco> enderecosEntrega = clienteForm.getEnderecosEntrega();
        if (enderecosEntrega != null) {
            for (Endereco endereco : enderecosEntrega) {
                endereco.setCliente(cliente); // Configura a relação bidirecional
            }
            cliente.setEnderecosEntrega(enderecosEntrega);
        }

        return cliente;
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
}
