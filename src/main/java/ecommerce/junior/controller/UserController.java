package ecommerce.junior.controller;

import ecommerce.junior.dto.ClienteForm;
import ecommerce.junior.model.*;
import ecommerce.junior.repository.ClienteRepository;
import ecommerce.junior.repository.UserRepository;
import ecommerce.junior.service.CarrinhoService;
import ecommerce.junior.service.ClienteService;
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
    private ClienteService clienteService;

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
    public String cadastrarCliente(@ModelAttribute("clienteForm") ClienteForm clienteForm,
                                   Model model, HttpSession session) {
        try {
            // Criação do cliente
            Cliente cliente = clienteService.createCliente(clienteForm);

            // Verifica se há um carrinho anônimo na sessão
            Carrinho carrinhoAnonimo = carrinhoService.obterOuCriarCarrinho(session);
            if (carrinhoAnonimo != null) {
                carrinhoService.associarCarrinhoAoCliente(carrinhoAnonimo, cliente);
                session.removeAttribute("carrinho"); // Limpa o carrinho da sessão
            }

            // Redireciona para o login após o cadastro bem-sucedido
            model.addAttribute("mensagem", "Cliente cadastrado com sucesso!");
            return "redirect:/admin/produtos/home";
        } catch (IllegalArgumentException e) {
            // Captura erros de validação
            model.addAttribute("mensagemErro", e.getMessage());
            return "cadastrar";
        } catch (Exception e) {
            // Lida com erros inesperados
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

    @GetMapping("/cadastrar-usuario")
    public String exibirFormularioCadastroUsuario(Model model) {
        model.addAttribute("user", new User());
        return "cadastrar-usuario";
    }

    @PostMapping("/cadastrar-usuario")
    public String cadastrarUsuario(
            @ModelAttribute("user") User user,
            @RequestParam("senhaConfirmacao") String senhaConfirmacao,
            Model model) {
        try {
            userService.createUser(user, senhaConfirmacao);
            model.addAttribute("mensagem", "Usuário cadastrado com sucesso!");
            return "redirect:/listar-usuario";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "cadastrar-usuario";
        }
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

    @GetMapping("/perfil")
    public String perfil(Model model) {
        // Obtém o cliente logado da sessão
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        if (clienteLogado == null) {
            return "redirect:/login"; // Redireciona para login se não estiver logado
        }

        // Adiciona o cliente logado no modelo para a página de perfil
        model.addAttribute("cliente", clienteLogado);

        // Adiciona os endereços para exibição
        model.addAttribute("enderecos", clienteLogado.getEnderecosEntrega());

        return "perfil";
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
