package ecommerce.junior.controller;

import ecommerce.junior.model.Endereco;
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

    @GetMapping("/principal")
    public String principal(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login"; // Redireciona se o usuário não estiver logado
        }
        return "principal"; // Retorna a página principal se o usuário estiver logado
    }
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        System.out.println("Usuário deslogado com sucesso.");
        return "redirect:/login";
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
        return "listar-usuario";
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

    // Mapeamento para a tela de cadastro de usuário
    @GetMapping("/cadastrar")
    public String showCadastroForm() {
        return "cadastrar"; // Nome do arquivo HTML para o formulário de cadastro
    }

    @PostMapping("/usuarios/cadastrar")
    public String cadastrarUsuario(
            @RequestParam("nome") String nome,
            @RequestParam("email") String email,
            @RequestParam("cpf") String cpf,
            @RequestParam("senha") String senha,
            @RequestParam("senhaConfirmacao") String senhaConfirmacao,
            @RequestParam("cep") String cep,
            @RequestParam("logradouro") String logradouro,
            @RequestParam("numero") String numero,
            @RequestParam("complemento") String complemento,
            @RequestParam("bairro") String bairro,
            @RequestParam("cidade") String cidade,
            @RequestParam("uf") String uf,
            Model model) {

        try {
            User user = new User(nome, email, cpf, senha, Grupo.CLIENTE);
            user.setEnderecoFaturamento(new Endereco(cep, logradouro, numero, complemento, bairro, cidade, uf));

            userService.createUser(user, senhaConfirmacao);
            model.addAttribute("message", "Usuário cadastrado com sucesso!");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "cadastrar";
        }
    }


}
