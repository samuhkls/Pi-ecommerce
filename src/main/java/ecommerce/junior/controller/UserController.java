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
        return "listar"; // Certifique-se de que há um template listar.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam String senhaConfirmacao, Model model) {
        try {
            userService.createUser(user, senhaConfirmacao);
            return "redirect:/usuarios";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "listar"; // Certifique-se de que há um template listar.html
        }
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, @RequestParam String senhaConfirmacao, Model model) {
        try {
            // Obtém o usuário logado do SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            System.out.println("User Details: " + userDetails.getUsername()); // Debugging

            // Recupera o usuário completo do banco de dados usando o nome
            User loggedInUser = userService.findByNome(userDetails.getUsername());
            System.out.println("Logged In User: " + loggedInUser); // Debugging

            // Atualiza o usuário com as informações fornecidas
            userService.updateUser(user, senhaConfirmacao, loggedInUser);

            return "redirect:/usuarios";
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            model.addAttribute("error", e.getMessage());
            return "listar"; // Verifique se há um template chamado listar.html
        }
    }

    @PostMapping("/status/{id}")
    public String alterarStatus(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserById(id);
            user.setAtivo(!user.isAtivo()); // Alterna o status

            // Obtém o usuário logado usando o username
            User loggedInUser = userService.findByNome(userDetails.getUsername());

            // Atualiza o usuário com o usuário logado
            userService.updateUser(user, loggedInUser.getSenha(), loggedInUser);

            return "redirect:/usuarios"; // Redireciona para a lista de usuários
        } catch (Exception e) {
            // Adicione algum tratamento de erro apropriado aqui
            return "redirect:/usuarios"; // Redireciona em caso de erro
        }
    }
}
