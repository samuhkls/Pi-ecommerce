package ecommerce.junior.controller;

import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private HttpSession session;

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

    @PostMapping("/cadastrar")
    public String registerUser(@ModelAttribute User user, @RequestParam String senhaConfirmacao, Model model) {
        try {
            if (user.getTipo() != null) {
                user.setTipo(user.getTipo());
            }

            userService.createUser(user, senhaConfirmacao);
            return "redirect:/listar-usuario";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "listar";
        }
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, HttpSession session, Model model) {
        try {
            if (user.getTipo() != null) {
                user.setTipo(user.getTipo());
            }
            userService.updateUser(user, session);
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

            userService.updateUser(user, session);

            return "redirect:/listar-usuario";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/usuarios";
        }
    }
}
