package ecommerce.junior.controller;

import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.Pedido;
import ecommerce.junior.model.StatusPedido;
import ecommerce.junior.model.User;
import ecommerce.junior.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/estoquista")
public class EstoquistaController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/listar-pedidos")
    public String listarPedidos(Model model, HttpSession session) {
        User usuarioLogado = (User) session.getAttribute("usuario");
        if (usuarioLogado == null || usuarioLogado.getTipo() != Grupo.ESTOQUISTA) {
            return "redirect:/login?erro=acesso_negado";
        }
        List<Pedido> pedidos = pedidoService.listarPedidosOrdenadosPorData();
        model.addAttribute("pedidos", pedidos);
        return "listar-pedidos"; // Nome da página HTML
    }


    @PostMapping("/alterar-status/{id}")
    public String alterarStatus(
            @PathVariable Long id,
            @RequestParam("status") StatusPedido novoStatus,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.atualizarStatusPedido(id, novoStatus);
            redirectAttributes.addFlashAttribute("mensagem", "Status atualizado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar status: " + e.getMessage());
        }
        return "redirect:/estoquista/listar-pedidos";
    }
}