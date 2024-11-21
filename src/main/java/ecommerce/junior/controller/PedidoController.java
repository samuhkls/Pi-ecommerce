package ecommerce.junior.controller;

import ecommerce.junior.model.Pedido;
import ecommerce.junior.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/pedido/resumo")
    public String exibirResumoDoPedido(HttpSession session, Model model) {
        // Recupera o pedido da sessão ou do banco de dados (caso já tenha sido salvo)
        Pedido pedido = (Pedido) session.getAttribute("pedido");

        if (pedido == null) {
            // Se o pedido não estiver na sessão, busca no banco de dados (por exemplo, pelo ID)
            Long pedidoId = (Long) session.getAttribute("pedidoId"); // Supondo que o ID do pedido esteja na sessão
            pedido = pedidoService.buscarPedidoPorId(pedidoId);
        }

        // Passa os dados do pedido para o modelo
        model.addAttribute("pedido", pedido);

        return "resumo-pedido";
    }

    @PostMapping("/pedido/concluir")
    public String concluirCompra(HttpSession session) {
        // Recupera o pedido da sessão
        Pedido pedido = (Pedido) session.getAttribute("pedido");

        if (pedido == null) {
            // Se o pedido não foi encontrado, redireciona para a tela de pagamento
            return "redirect:/carrinho/pagamento";
        }

        // Atualiza o status do pedido para "Aguardando Pagamento" ou outro status desejado
        pedido.setStatus("Concluído");
        pedidoService.salvarPedido(pedido); // Salva o pedido no banco de dados

        // Limpa a sessão após a compra ser concluída
        session.removeAttribute("pedido");

        // Redireciona para a tela de confirmação de pedido
        return "redirect:/pedido/confirmacao";
    }

    @Controller
    public class PagamentoController {

        @Autowired
        private PedidoService pedidoService;

        @PostMapping("/processar-pagamento")
        public String processarPagamento(@RequestParam("formaPagamento") String formaPagamento,
                                         HttpSession session) {

            Pedido pedido = (Pedido) session.getAttribute("pedido");
            if (pedido == null) {
                return "redirect:/carrinho/pagamento";  // Se o pedido não existe, redireciona para a página de pagamento
            }

            pedido.setFormaPagamento(formaPagamento);

            // Salva o pedido com a forma de pagamento
            pedidoService.salvarPedido(pedido);

            // Redireciona para o resumo do pedido
            return "redirect:/pedido/resumo-pedido";
        }
    }

}

