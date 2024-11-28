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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
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
        model.addAttribute("produtos", pedido.getProdutos()); // Adiciona os produtos do pedido à view
        return "resumo-pedido";
    }

    @PostMapping("/gerar-pedido")
    public String gerarPedido(
            @RequestParam("formaPagamento") String formaPagamento,
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
        pedido.setFormaPagamento(formaPagamento);
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        // Verificar endereço de entrega
        if (cliente.getEnderecosEntrega() == null || cliente.getEnderecosEntrega().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagem", "Nenhum endereço de entrega encontrado.");
            return "redirect:/carrinho";
        }
        pedido.setEnderecoEntrega(cliente.getEnderecosEntrega().get(0));

        pedido.setValorFrete(20.0);
        pedido.setNumeroPedido(System.currentTimeMillis());

        List<ProdutoPedido> produtosPedido = carrinho.getProdutos().entrySet().stream().map(entry -> {
            Long produtoId = entry.getKey(); // ID do produto
            Integer quantidade = entry.getValue(); // Quantidade do produto

            // Buscar o produto pelo ID
            Produto produto = buscarProdutoPorId(produtoId); // Certifique-se de ter este método no código

            ProdutoPedido produtoPedido = new ProdutoPedido();
            produtoPedido.setProduto(produto); // Define o produto associado
            produtoPedido.setQuantidade(quantidade); // Define a quantidade
            produtoPedido.setSubtotal(produto.getPreco() * quantidade); // Calcula o subtotal
            return produtoPedido;
        }).toList();

        pedido.setProdutos(produtosPedido);
        pedidoService.salvarPedido(pedido);

        session.setAttribute("pedidoId", pedido.getId());
        session.removeAttribute("carrinho");

        return "redirect:/pedido/resumo";
    }

    @PostMapping("/pedido/confirmar-compra")
    public String confirmarCompra(HttpSession session, RedirectAttributes redirectAttributes) {
        Long pedidoId = (Long) session.getAttribute("pedidoId");
        if (pedidoId == null) {
            redirectAttributes.addFlashAttribute("mensagem", "Nenhum pedido encontrado.");
            return "redirect:/carrinho";
        }
        pedidoService.atualizarStatusPedido(pedidoId, StatusPedido.AGUARDANDO_PAGAMENTO);
        Pedido pedido = pedidoService.buscarPedidoPorId(pedidoId);

        redirectAttributes.addFlashAttribute("mensagemSucesso", true); // Indicador para abrir modal
        redirectAttributes.addFlashAttribute("pedido", pedido); // Dados do pedido para exibição
        return "redirect:/pedido/resumo";
    }

    @GetMapping("/listar-pedidos")
    public String listarPedidos(Model model, HttpSession session) {
        User usuarioLogado = (User) session.getAttribute("usuario");
        if (usuarioLogado == null || usuarioLogado.getTipo() != Grupo.ESTOQUISTA) {
            return "redirect:/login?erro=acesso_negado";
        }
        return "listar-pedidos";
    }

    @GetMapping("/meus-pedidos")
    public String listarPedidosDoCliente(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidos = pedidoService.listarPedidosPorCliente(cliente.getId());
        model.addAttribute("pedidos", pedidos);
        return "meus-pedidos";
    }
    private Produto buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: ID " + id));
    }

    @GetMapping("/pedido/detalhes/{id}")
    public String exibirDetalhesPedido(@PathVariable Long id, Model model, HttpSession session) {
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        if (clienteLogado == null) {
            return "redirect:/login";  // Redireciona para o login se não estiver logado
        }

        try {
            Pedido pedido = pedidoService.buscarPedidoPorId(id);

            if (pedido == null || !pedido.getCliente().getId().equals(clienteLogado.getId())) {
                model.addAttribute("error", "Pedido não encontrado ou acesso não autorizado.");
                return "redirect:/usuarios/perfil";  // Redireciona para o perfil do usuário se o pedido não for encontrado ou o acesso for negado
            }

            model.addAttribute("pedido", pedido);
            return "detalhes-pedido";  // Retorna para a página de detalhes do pedido
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erro ao carregar o pedido. Tente novamente mais tarde.");
            return "erro";  // Retorna para uma página de erro em caso de exceção
        }
    }


}
