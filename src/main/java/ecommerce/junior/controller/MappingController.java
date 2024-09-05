package ecommerce.junior.controller;

import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;



@Controller
public class MappingController {



    @Autowired
    private UserService userService;
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/principal")
    public String principal() {
        return "principal";
    }

    @GetMapping("/listar-usuario")
    public String listarUsuario(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "lista-usuario";
    }

    @PostMapping("/update-user")
    public String updateUser(@RequestParam Long id, @RequestParam String nome, @RequestParam String email,
                             @RequestParam Boolean ativo, @RequestParam String senha, @RequestParam String senhaConfirmacao,
                             Model model) {
        try {
            User user = userService.getUserById(id);
            user.setNome(nome);
            user.setEmail(email);
            user.setAtivo(true);
            userService.updateUser(user, senhaConfirmacao, user);  // Assuming the logged-in user is the one making the update
            model.addAttribute("message", "Usuário atualizado com sucesso!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/listar-usuario";
    }
}
