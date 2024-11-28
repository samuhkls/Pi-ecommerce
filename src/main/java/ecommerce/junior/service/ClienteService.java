package ecommerce.junior.service;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import ecommerce.junior.dto.ClienteForm;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Endereco;
import ecommerce.junior.model.User;
import ecommerce.junior.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final HttpSession session;

    public ClienteService(HttpSession session) {
        this.session = session;
    }


    public Cliente authenticate(String email, String senha) {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente != null && cliente.getSenha().equals(senha)) {
            session.setAttribute("clienteLogado", cliente);
            return cliente;
        }
        return null;
    }

    public Cliente createCliente(ClienteForm clienteForm) throws IllegalArgumentException {
        if (!isNomeValido(clienteForm.getNome())) {
            throw new IllegalArgumentException("Este nome não é válido!");
        }

        if (!clienteForm.isSenhaConfirmada()) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        if (clienteRepository.findByEmail(clienteForm.getEmail()) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        if (clienteRepository.findByCpf(clienteForm.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        CPFValidator cpfValidator = new CPFValidator();
        try {
            cpfValidator.assertValid(clienteForm.getCpf());
        } catch (InvalidStateException e) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        // Codifica a senha
       // String senhaCodificada = passwordEncoder.encode(clienteForm.getSenha());

        // Cria o cliente
        Cliente cliente = fromDTO(clienteForm);
        //cliente.setSenha(senhaCodificada);

        // Salva o cliente no repositório
        return clienteRepository.save(cliente);
    }

    public List<Endereco> listarEnderecos(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clienteId));
        return cliente.getEnderecosEntrega();
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
