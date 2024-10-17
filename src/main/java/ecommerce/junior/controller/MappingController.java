package ecommerce.junior.controller;

import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class MappingController {

    @Autowired
    private HttpSession session;
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastrocliente")
    public String exibirCadastroCliente(Model model) {
        model.addAttribute("user", new User());  // Adiciona um objeto User vazio ao modelo
        return "cadastrocliente";  // Nome da página HTML para o cadastro de cliente
    }
    @PostMapping("/cadastrocliente")
    public String cadastrarCliente(Model model) {
        try {
            // Criando um novo objeto User e atribuindo valores diretamente
            User user = new User();
            user.setNome("muu rilo");
            user.setCpf("12345678909");
            user.setEmail("liro@penris.com");
            user.setSenha("password");
            user.setEndereco("01311-000, Avenida Paulista, 1000, Sala 202, Bela Vista, São Paulo, SP");
            user.setTipo(Grupo.ADMINISTRADOR); // Aqui você pode definir o tipo que preferir

            // Chama o serviço para salvar o usuário
            userService.createUser(user, user.getSenha());

            // Adiciona mensagem de sucesso ao modelo para feedback na página
            model.addAttribute("message", "Usuário cadastrado com sucesso!");
            return "redirect:/listausuario"; // Redireciona para a lista de usuários ou outra página de sucesso
        } catch (Exception e) {
            // Caso ocorra um erro, exibe a mensagem de erro
            model.addAttribute("error", "Erro ao cadastrar o usuário: " + e.getMessage());
            return "cadastrocliente"; // Retorna à página de cadastro em caso de erro
        }
    }

    @GetMapping("/principal")
    public String principal() {
        return "principal";
    }

    @GetMapping("/listar-usuario")
    public String listarUsuario(@RequestParam(value = "nome", required = false) String nome, Model model) {
        List<User> users;
        if (nome != null && !nome.isEmpty()) {
            users = userService.findUsersByName(nome);
        } else {
            users = userService.getAllUsers();
        }
        model.addAttribute("users", users);
        return "lista-usuario";
    }

    @GetMapping("/editar/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        try {
            User user = userService.findById(id);

            if (user == null) {
                model.addAttribute("errorMessage", "Usuário não encontrado");
                return "error-page";
            }

            model.addAttribute("user", user);
            return "editar-usuario";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Ocorreu um erro ao processar a solicitação.");
            return "error-page";
        }
    }

    @PostMapping("/updateDetails")
    public String updateUser(@RequestParam Long id, @RequestParam String nome, @RequestParam String email, @RequestParam String senhaConfirmacao,
                             Model model) {
        try {
            User user = userService.getUserById(id);
            user.setNome(nome);
            user.setEmail(email);

            userService.updateUser(user, session);

            model.addAttribute("message", "Usuário atualizado com sucesso!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/listar-usuario";
    }

    @PostMapping("/alterar-status/{id}")
    public String alterarStatus(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            user.setAtivo(!user.isAtivo());

            userService.updateUser(user, session);
            return "redirect:/listar-usuario";
        } catch (Exception e) {
            return "redirect:/listar-usuario";
        }
    }
}
