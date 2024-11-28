package ecommerce.junior.controller;

import ecommerce.junior.dto.ClienteForm;
import ecommerce.junior.model.*;
import ecommerce.junior.repository.ClienteRepository;
import ecommerce.junior.service.*;
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
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/principal")
    public String principal(Model model) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }
            return "principal";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login";
        }
    }

    @GetMapping("/listar-produtos")
    public String listarProdutos(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produto> produtosPage = produtoService.getAllProdutos(pageable);
        model.addAttribute("produtos", produtosPage != null ? produtosPage.getContent() : List.of());
        return "lista-produtos";
    }

    @GetMapping("/listar-usuarios")
    public String listarUsuarios(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "listarUsuarios";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao listar usuários.");
            return "error";
        }
    }

    @GetMapping("/listar-pedidos")
    public String listarPedidos(Model model) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }

            List<Pedido> pedidos = pedidoService.listarPedidosPorCliente(userId);
            model.addAttribute("pedidos", pedidos != null ? pedidos : List.of());
            return "listarPedidos";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erro ao listar pedidos.");
            return "error";
        }
    }

    @GetMapping("/cadastrar")
    public String cadastrar() {
        return "cadastrar";
    }

    @PostMapping("/cadastrar")
    public String cadastrarCliente(
            @ModelAttribute("clienteForm") ClienteForm clienteForm,
            Model model) {
        try {
            Cliente cliente = clienteService.createCliente(clienteForm);

            Carrinho carrinhoAnonimo = carrinhoService.obterOuCriarCarrinho(session);
            if (carrinhoAnonimo != null) {
                carrinhoService.associarCarrinhoAoCliente(carrinhoAnonimo, cliente);
                session.removeAttribute("carrinho");
            }

            model.addAttribute("mensagem", "Cliente cadastrado com sucesso!");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            return "cadastrar";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensagemErro", "Erro ao cadastrar cliente.");
            return "cadastrar";
        }
    }

    @PostMapping("/status/{id}")
    public String alterarStatus(@PathVariable Long id) {
        try {
            Long currentUserId = (Long) session.getAttribute("userId");
            if (currentUserId == null) {
                throw new Exception("Usuário não está logado.");
            }

            User user = userService.getUserById(id);
            if (user == null) {
                throw new Exception("Usuário não encontrado.");
            }

            User currentUser = userService.getUserById(currentUserId);
            if (!user.getId().equals(currentUser.getId())) {
                throw new Exception("Você não tem permissão para alterar o status de outros usuários.");
            }

            user.setAtivo(!user.isAtivo());
            userService.updateUser(user, "", "", session);

            return "redirect:/listar-usuarios";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/usuarios";
        }
    }
}
