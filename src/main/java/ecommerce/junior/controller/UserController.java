package ecommerce.junior.controller;

import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listarUsuarios(@RequestParam(value = "nome", required = false) String nome, Model model) {
        List<User> users = userService.getUsersByName(nome);
        model.addAttribute("users", users);
        return "listar";
    }

    @PostMapping("/cadastrar")
    public String cadastrarUsuario(@ModelAttribute("user") User user, @RequestParam("senhaConfirmacao") String senhaConfirmacao, Model model) {
        if (!user.getSenha().equals(senhaConfirmacao)) {
            model.addAttribute("error", "As senhas não coincidem.");
            return "cadastrar-usuario";
        }
        // Lógica para salvar o usuário
        return "redirect:/usuarios/listar";
    }


    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam String senhaConfirmacao, Model model) {
        try {
            userService.createUser(user, senhaConfirmacao);
            return "redirect:/usuarios";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "cadastrar-usuario"; // Volta ao formulário em caso de erro
        }
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, @RequestParam String senhaConfirmacao, Model model) {
        try {
            // Obtém o usuário logado do SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User loggedInUser = userService.findByNome(userDetails.getUsername());

            userService.updateUser(user, senhaConfirmacao, loggedInUser);

            return "redirect:/usuarios";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "listar";
        }
    }

    @PostMapping("/alterar-status")
    public String alterarStatus(@RequestParam Long id, @AuthenticationPrincipal UserDetails userDetails) {
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
