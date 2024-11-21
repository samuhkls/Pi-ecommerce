package ecommerce.junior.controller;

import ecommerce.junior.model.Pedido;
import ecommerce.junior.model.StatusPedido;
import ecommerce.junior.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/estoquista")
public class EstoquistaController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/listar-pedidos")
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        model.addAttribute("pedidos", pedidos);
        return "listar-pedidos"; // Nome da página HTML
    }

    @PostMapping("/alterar-status/{id}")
    public String alterarStatus(@PathVariable Long id, @RequestParam("status") StatusPedido novoStatus) {
        try {
            pedidoService.alterarStatus(id, novoStatus);
            return "redirect:/estoquista/listar-pedidos";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/estoquista/listar-pedidos?erro=" + e.getMessage();
        }
    }
}