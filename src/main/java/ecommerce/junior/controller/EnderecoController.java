package ecommerce.junior.controller;

import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Endereco;
import ecommerce.junior.repository.EnderecoRepository;
import ecommerce.junior.service.ClienteService;
import ecommerce.junior.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private HttpSession session;

    // Método para exibir o formulário de cadastro de endereço
    @GetMapping("/enderecos/cadastrar")
    public String exibirFormEndereco(Model model) {
        // Cria um novo objeto Endereco para o formulário
        model.addAttribute("endereco", new Endereco());
        return "cadastrarEndereco";  // Retorna o nome do template de cadastro de endereço
    }

    // Método para cadastrar o endereço
    @PostMapping("/enderecos/cadastrar")
    public String cadastrarEndereco(@ModelAttribute Endereco endereco) {
        // Obtém o cliente logado
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        if (clienteLogado != null) {
            // Atribui o cliente ao endereço
            endereco.setCliente(clienteLogado);
            clienteLogado.getEnderecosEntrega().add(endereco);  // Adiciona o endereço na lista de endereços do cliente

            // Salva o endereço no banco de dados
            enderecoRepository.save(endereco);

            // Salva o cliente com o novo endereço
            clienteService.salvar(clienteLogado);
        }

        // Redireciona para o perfil do cliente após cadastrar o endereço
        return "redirect:/admin/produtos/home";
    }


    // Rota para editar um endereço
    @GetMapping("/enderecos/editar/{id}")
    public String editarEnderecoForm(@PathVariable Long id, Model model) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado: " + id));
        model.addAttribute("endereco", endereco);
        return "editarEndereco";  // Nome do template de edição de endereço
    }

    // Rota para salvar as alterações do endereço
    @PostMapping("/enderecos/editar/{id}")
    public String editarEndereco(@PathVariable Long id, @ModelAttribute Endereco enderecoAtualizado) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado: " + id));
        endereco.setCep(enderecoAtualizado.getCep());
        endereco.setLogradouro(enderecoAtualizado.getLogradouro());
        endereco.setNumero(enderecoAtualizado.getNumero());
        endereco.setComplemento(enderecoAtualizado.getComplemento());
        endereco.setBairro(enderecoAtualizado.getBairro());
        endereco.setLocalidade(enderecoAtualizado.getLocalidade());
        endereco.setUf(enderecoAtualizado.getUf());
        enderecoRepository.save(endereco);
        return "redirect:/perfil";  // Redireciona para o perfil após a edição
    }
}



