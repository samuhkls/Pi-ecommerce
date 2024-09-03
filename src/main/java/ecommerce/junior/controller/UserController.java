package ecommerce.junior.controller;

import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user, @RequestParam String senhaConfirmacao) {
        try {
            userService.createUser(user, senhaConfirmacao);
            return "Usuário cadastrado com sucesso!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PutMapping("/update")
    public String updateUser(@RequestBody User user, @RequestParam String senhaConfirmacao, @RequestParam Long loggedInUserId) {
        try {
            User loggedInUser = userService.getUserById(loggedInUserId);
            userService.updateUser(user, senhaConfirmacao, loggedInUser);
            return "Usuário atualizado com sucesso!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
