package ecommerce.junior.controller;

import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String senha) {
        User user = userService.authenticate(email, senha);
        if (user != null) {
            session.setAttribute("userId", user.getId());
            return "redirect:/listar-usuario";
        } else {
            return "login";
        }
    }
}

