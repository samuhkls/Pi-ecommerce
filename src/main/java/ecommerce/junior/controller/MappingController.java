package ecommerce.junior.controller;

import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

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
    public String editUser(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findById(id);
            User loggedInUser = userService.findByNome(userDetails.getUsername());

            if (user == null) {
                model.addAttribute("errorMessage", "Usuário não encontrado");
                return "error-page";
            }

            model.addAttribute("user", user);
            model.addAttribute("loggedInUser", loggedInUser);
            return "editar-usuario";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Ocorreu um erro ao processar a solicitação.");
            return "error-page";
        }
    }




    @PostMapping("/updateDetails")
    public String updateUser(@RequestParam Long id, @RequestParam String nome, @RequestParam String email,
                             @RequestParam String senha, @RequestParam String senhaConfirmacao,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        try {
            User user = userService.getUserById(id);
            user.setNome(nome);
            user.setEmail(email);

            User loggedInUser = userService.findByNome(userDetails.getUsername());

            userService.updateUser(user, senhaConfirmacao, loggedInUser);

            model.addAttribute("message", "Usuário atualizado com sucesso!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/listar-usuario";
    }

    @PostMapping("/alterar-status/{id}")
    public String alterarStatus(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserById(id);
            user.setAtivo(!user.isAtivo()); // Alterna o status

            User loggedInUser = userService.findByNome(userDetails.getUsername());

            userService.updateUser(user, loggedInUser.getSenha(), loggedInUser);

            return "redirect:/listar-usuario";
        } catch (Exception e) {
            return "redirect:/listar-usuario";
        }
    }
}