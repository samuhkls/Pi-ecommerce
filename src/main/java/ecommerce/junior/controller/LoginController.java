package ecommerce.junior.controller;

import ecommerce.junior.model.Carrinho;
import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.User;
import ecommerce.junior.service.CarrinhoService;
import ecommerce.junior.service.ClienteService;
import ecommerce.junior.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private HttpSession session;

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
        Cliente cliente = clienteService.authenticate(email, senha);

        if (cliente != null) {
            session.setAttribute("clienteId", cliente.getId());
            Carrinho carrinho = carrinhoService.getCarrinhoByClienteId(cliente);
            if (carrinho == null) {
                carrinho = new Carrinho();
                carrinho.setCliente(cliente);
                carrinhoService.salvarCarrinho(carrinho);
            }

            session.setAttribute("carrinhoId", carrinho.getId());

            return "redirect:/admin/produtos/home";
        }

        User user = userService.authenticate(email, senha);
        if (user != null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("usuario", user);
            return "redirect:/principal";
        }

        model.addAttribute("erro", "Credenciais inválidas!");
        return "login";
    }

}

