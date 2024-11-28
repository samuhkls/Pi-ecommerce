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
import org.hibernate.Hibernate;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EnderecoController {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private HttpSession session;

    @GetMapping("/buscar-endereco")
    @ResponseBody
    public Endereco buscarEndereco(@RequestParam String cep) {
        try {
            String url = "https://viacep.com.br/ws/" + cep + "/json/";
            return enderecoService.buscarEndereco(cep);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/enderecos/editar/{id}")
    public String exibirEditarForm(@PathVariable Long id, Model model) {
        // Recupera o endereço pelo id
        Endereco endereco = enderecoRepository.findById(id).orElse(null);

        // Verifica se o endereço existe
        if (endereco == null) {
            return "redirect:/admin/produtos/home";  // Redireciona para a página inicial, caso não encontre o endereço
        }

        // Passa o objeto Endereco para a view, para que o Thymeleaf possa preencher o formulário
        model.addAttribute("endereco", endereco);

        return "editarEndereco";  // Retorna o nome da página do formulário de edição
    }

    @PostMapping("/enderecos/editar/{id}")
    public String editarEndereco(@PathVariable Long id, @ModelAttribute Endereco endereco) {
        // Recupera o endereço pelo id
        Endereco enderecoExistente = enderecoRepository.findById(id).orElse(null);

        // Verifica se o endereço existe
        if (enderecoExistente == null) {
            return "redirect:/admin/produtos/home";  // Redireciona para a página inicial, caso não encontre o endereço
        }

        // Atualiza os dados do endereço
        enderecoExistente.setCep(endereco.getCep());
        enderecoExistente.setLogradouro(endereco.getLogradouro());
        enderecoExistente.setNumero(endereco.getNumero());
        enderecoExistente.setComplemento(endereco.getComplemento());
        enderecoExistente.setBairro(endereco.getBairro());
        enderecoExistente.setLocalidade(endereco.getLocalidade());
        enderecoExistente.setUf(endereco.getUf());

        // Salva o endereço atualizado no banco de dados
        enderecoRepository.save(enderecoExistente);

        // Recupera o cliente logado da sessão usando o clienteId armazenado
        Long clienteId = (Long) session.getAttribute("clienteId");  // Recupera o clienteId da sessão

        // Verifica se o clienteId está presente na sessão
        if (clienteId != null) {
            // Recupera o cliente pelo ID
            Cliente clienteLogado = clienteService.getClienteById(clienteId);

            // Verifica se o cliente foi encontrado
            if (clienteLogado != null) {
                // Redireciona para o perfil do cliente
                return "redirect:/admin/produtos/perfil/" + clienteLogado.getId();
            }
        }

        // Caso o cliente não esteja logado ou o clienteId não esteja presente, redireciona para a página de login
        return "redirect:/login";
    }




    // Método para exibir o formulário de cadastro de endereço
    @GetMapping("/enderecos/cadastrar")
    public String exibirFormularioEndereco(Model model) {
        // Cria um objeto Endereco vazio para preencher o formulário
        Endereco endereco = new Endereco();

        // Passa o objeto Endereco para a view, para que o Thymeleaf possa renderizar o formulário
        model.addAttribute("endereco", endereco);

        // Retorna o nome da página do formulário (exemplo: "cadastrarEndereco.html")
        return "cadastrarEndereco";
    }

    @PostMapping("/enderecos/cadastrar")
    public String cadastrarEndereco(@ModelAttribute Endereco endereco) {
        Long clienteId = (Long) session.getAttribute("clienteId");

        // Verifica se o cliente está logado
        Cliente clienteLogado;
        if (clienteId != null) {
            clienteLogado = clienteService.getClienteById(clienteId);

            if (clienteLogado != null) {
                // Atribui o cliente ao endereço
                endereco.setCliente(clienteLogado);

                // Salva o endereço no banco de dados
                enderecoRepository.save(endereco);

                // Atualiza a lista de endereços no cliente, se necessário
                if (clienteLogado.getEnderecosEntrega() == null) {
                    clienteLogado.setEnderecosEntrega(new ArrayList<>());
                }
                clienteLogado.getEnderecosEntrega().add(endereco);

                // Não é necessário salvar o cliente explicitamente, já que a relação foi persistida
            }
        } else {
            // Se o cliente não estiver logado, redireciona para a página de login
            return "redirect:/login";
        }

        // Redireciona para o perfil do cliente após cadastrar o endereço
        return "redirect:/admin/produtos/perfil/" + clienteLogado.getId();
    }

}




