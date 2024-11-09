package ecommerce.junior.controller;

import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.Produto;
import ecommerce.junior.model.User;
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

    // endpoint para a pagina principal do backoffice, que será diferente com base no tipo do usuario
    @GetMapping("/principal")
    public String principal(Model model) {
        try {
            // Recuperando o ID do usuário da sessão
            Long currentUserId = (Long) session.getAttribute("userId");

            // Verificando se o usuário está logado
            if (currentUserId == null) {
                throw new Exception("Usuário não está logado.");
            }

            // Recuperando o grupo do usuário a partir do UserService
            Grupo userRole = userService.getUserRole(currentUserId);

            // Adicionando os atributos no modelo
            model.addAttribute("userRole", userRole);  // Passando o grupo do usuário
            model.addAttribute("userId", currentUserId);  // Passando o ID do usuário

            // Retornando a página principal
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
