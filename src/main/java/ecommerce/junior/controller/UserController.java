package ecommerce.junior.controller;

import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam String senhaConfirmacao, Model model) {
        try {
            userService.createUser(user, senhaConfirmacao);
            return "redirect:/usuarios";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "listar";
        }
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, @RequestParam String senhaConfirmacao, Model model) {
        try {
            userService.updateUser(user, senhaConfirmacao, user);

            return "redirect:/usuarios";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "listar";
        }
    }

    @PostMapping("/status/{id}")
    public String alterarStatus(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            user.setAtivo(!user.isAtivo());

            userService.updateUser(user, user.getSenha(), user);

            return "redirect:/usuarios";
        } catch (Exception e) {
            return "redirect:/usuarios";
        }
    }
}
