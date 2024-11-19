package ecommerce.junior.controller;

import ecommerce.junior.dto.ClienteForm;
import ecommerce.junior.model.*;
import ecommerce.junior.repository.ClienteRepository;
import ecommerce.junior.repository.UserRepository;
import ecommerce.junior.service.CarrinhoService;
import ecommerce.junior.service.ProdutoService;
import ecommerce.junior.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private HttpSession session;

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private ClienteRepository clienteRepository;

    // endpoint para a pagina principal do backoffice, que será diferente com base no tipo do usuario
    @GetMapping("/principal")
    public String principal(Model model) {
        try {
            return "principal";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login";  // Redireciona ao login se ocorrer algum erro
        }
    }


    // acessível para os dois tipos de usuários
    @GetMapping("/listar-produtos")
    public String listarProdutos(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produto> produtosPage = produtoService.getAllProdutos(pageable);
        model.addAttribute("produtos", produtosPage.getContent());
        return "lista-produtos";
    }


    // acessível apenas para administradores
    @GetMapping("/listar-usuarios")
    public String listarUsuarios(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "listarUsuarios";
    }

    // acessível apenas para estoquistas
    @GetMapping("/listar-pedidos")
    public String listarPedidos(Model model) {
        // Implementação de listagem de pedidos (caso tenha um serviço ou lógica para isso)
        return "listarPedidos";
    }

    @GetMapping("/cadastrar")
    public String cadastrar(){
        return "cadastrar";
    }

    @PostMapping("/cadastrar")
    public String cadastrarCliente(@ModelAttribute("clienteForm") ClienteForm clienteForm, Model model) {
        try {
            Cliente cliente = new Cliente();
            cliente.setNome(clienteForm.getNome());
            cliente.setEmail(clienteForm.getEmail());
            cliente.setCpf(clienteForm.getCpf());
            cliente.setSenha(clienteForm.getSenha());
            cliente.setEnderecoFaturamento(clienteForm.getEnderecoFaturamento());

            cliente.setEnderecosEntrega(clienteForm.getEnderecosEntrega());

            clienteRepository.save(cliente);

            // Criar o carrinho e associar ao cliente
            Carrinho carrinho = new Carrinho();
            carrinho.setCliente(cliente);
            carrinhoService.salvarCarrinho(carrinho); // Salvar carrinho no banco

            // Salvar o carrinho com os itens associados
            model.addAttribute("mensagem", "Cliente cadastrado com sucesso!");
            session.setAttribute("cliente", cliente);

            model.addAttribute("mensagem", "Cliente cadastrado com sucesso!");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao cadastrar cliente: " + e.getMessage());
            return "cadastrar";
        }
    }
    @GetMapping
    public String listarUsuarios(@RequestParam(value = "nome", required = false) String nome, Model model) {
        List<User> users = userService.getUsersByName(nome);
        model.addAttribute("users", users);
        return "listar";
    }



    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, HttpSession session, Model model) {
        try {
            if (user.getTipo() != null) {
                user.setTipo(user.getTipo());
            }
            userService.updateUser(user,"", "", session);
            return "redirect:/listar-usuario";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "listar";
        }
    }

    @PostMapping("/status/{id}")
    public String alterarStatus(@PathVariable Long id) {
        try {
            Long currentUserId = (Long) session.getAttribute("userId");
            if(currentUserId == null){
                throw new Exception("Usuário não está logado.");
            }

            User user = userService.getUserById(id);
            if(user == null){
                throw new Exception("Usuário não encontrado");
            }

            User currentUser = userService.getUserById(currentUserId);
            if(!user.getId().equals(currentUser.getId())){
                throw new Exception("Você não tem permissão de alterar o status dos outros usuários.");
            }

            user.setAtivo(!user.isAtivo());

            userService.updateUser(user, "", "",session);

            return "redirect:/listar-usuario";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/usuarios";
        }
    }
}
