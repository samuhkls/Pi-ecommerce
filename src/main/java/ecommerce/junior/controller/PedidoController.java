package ecommerce.junior.controller;

import ecommerce.junior.model.*;
import ecommerce.junior.repository.ProdutoRepository;
import ecommerce.junior.service.CarrinhoService;
import ecommerce.junior.service.ClienteService;
import ecommerce.junior.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/pedido/resumo")
    public String exibirResumoDoPedido(HttpSession session, Model model) {
        Long pedidoId = (Long) session.getAttribute("pedidoId");

        if (pedidoId == null) {
            model.addAttribute("mensagem", "Nenhum pedido encontrado.");
            return "redirect:/carrinho";
        }

        Pedido pedido = pedidoService.buscarPedidoPorId(pedidoId);

        if (pedido == null) {
            model.addAttribute("mensagem", "Pedido inválido ou inexistente.");
            return "redirect:/carrinho";
        }

        model.addAttribute("pedido", pedido);
        return "resumo-pedido";
    }

    @PostMapping("/gerar-pedido")
    public String gerarPedido(
            @RequestParam("formaPagamento") String formaPagamento, // Captura a forma de pagamento do formulário
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Carrinho carrinho = carrinhoService.obterOuCriarCarrinho(session);

        if (carrinho == null || carrinho.getProdutos().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagem", "Seu carrinho está vazio.");
            return "redirect:/carrinho";
        }

        Cliente cliente = carrinho.getCliente();
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("mensagem", "Cliente não identificado. Faça login novamente.");
            return "redirect:/login";
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setValorTotal(carrinho.getTotal());
        pedido.setFormaPagamento(formaPagamento); // Define a forma de pagamento diretamente no pedido

        List<ProdutoPedido> produtosPedido = carrinho.getProdutos().entrySet().stream().map(entry -> {
            Produto produto = buscarProdutoPorId(entry.getKey());
            ProdutoPedido produtoPedido = new ProdutoPedido();
            produtoPedido.setProduto(produto);
            produtoPedido.setQuantidade(entry.getValue());
            return produtoPedido;
        }).toList();
        pedido.setProdutos(produtosPedido);

        if (cliente.getEnderecosEntrega() != null && !cliente.getEnderecosEntrega().isEmpty()) {
            pedido.setEnderecoEntrega(cliente.getEnderecosEntrega().get(0));
        } else {
            redirectAttributes.addFlashAttribute("mensagem", "Nenhum endereço de entrega encontrado.");
            return "redirect:/carrinho";
        }

        double valorFrete = 20;
        pedido.setValorFrete(valorFrete);
        pedido.setNumeroPedido(System.currentTimeMillis());

        pedidoService.salvarPedido(pedido);

        session.setAttribute("pedidoId", pedido.getId());
        session.removeAttribute("carrinho");

        return "redirect:/pedido/resumo";
    }

    private Produto buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: ID " + id));
    }
}
