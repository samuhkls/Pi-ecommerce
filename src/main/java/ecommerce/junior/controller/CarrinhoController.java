package ecommerce.junior.controller;

import ecommerce.junior.model.*;
import ecommerce.junior.repository.CarrinhoRepository;
import ecommerce.junior.repository.PedidoRepository;
import ecommerce.junior.service.CarrinhoService;
import ecommerce.junior.service.ClienteService;
import ecommerce.junior.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("carrinho")
public class CarrinhoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @ModelAttribute("carrinho")
    public Carrinho inicializarCarrinho(HttpSession session) {
        System.out.println("Inicializando carrinho na sessão...");
        return carrinhoService.obterOuCriarCarrinho(session);
    }

    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionarProdutoAoCarrinho(@PathVariable Long id,
                                             HttpSession session,
                                             Model model) {
        // Obtenha ou crie o carrinho na sessão
        Carrinho carrinho = carrinhoService.obterOuCriarCarrinho(session);

        Optional<Produto> produtoOpt = produtoService.getProdutoById(id);

        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            carrinho.adicionarProduto(produto.getId(), 1, produto.getPreco()); // Adiciona 1 unidade como padrão
            session.setAttribute("carrinho", carrinho); // Atualiza o carrinho na sessão
            carrinhoRepository.save(carrinho); // Salva as alterações no banco de dados
        }

        model.addAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho")
    public String visualizarCarrinho(HttpSession session, Model model) {
        Long carrinhoId = (Long) session.getAttribute("carrinhoId");
        Carrinho carrinho;

        if (carrinhoId == null) {
            // Criar um novo carrinho
            carrinho = carrinhoService.obterOuCriarCarrinho();
            session.setAttribute("carrinhoId", carrinho.getId());
        } else {
            try {
                carrinho = carrinhoService.obterCarrinhoPorId(carrinhoId);
            } catch (RuntimeException e) {
                // Criar um novo carrinho se o anterior não existir
                carrinho = carrinhoService.obterOuCriarCarrinho();
                session.setAttribute("carrinhoId", carrinho.getId());
            }
        }

        // Processamento dos dados do carrinho
        Map<Long, Integer> produtosCarrinho = carrinho.getProdutos();
        List<Produto> produtos = produtoService.getProdutosByIds(produtosCarrinho.keySet());

        Map<Long, Produto> produtosPorId = produtos.stream()
                .collect(Collectors.toMap(Produto::getId, produto -> produto));

        double total = calcularTotal(produtosCarrinho, produtosPorId);

        model.addAttribute("carrinho", produtosCarrinho);
        model.addAttribute("produtos", produtosPorId);
        model.addAttribute("total", total);

        return "carrinho";
    }



    @GetMapping("/carrinho/pagamento")
    public String exibirPagamento(HttpSession session, Model model) {

        Carrinho carrinho = carrinhoService.obterOuCriarCarrinho(session);
        Cliente cliente = carrinho.getCliente();

        model.addAttribute("cliente", cliente);

        return "pagamento";
    }


    @PostMapping("/carrinho/atualizar/{id}")
    public String atualizarQuantidade(@PathVariable Long id,
                                      @RequestParam Integer quantidade,
                                      HttpSession session,
                                      Model model) {
        Carrinho carrinho = carrinhoService.obterOuCriarCarrinho(session);
        Optional<Produto> produtoOpt = produtoService.getProdutoById(id);

        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            carrinho.atualizarQuantidade(produto.getId(), quantidade);
            carrinhoRepository.save(carrinho);
        }

        model.addAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/finalizar")
    public String finalizarPedido(HttpSession session, Model model) {
        Carrinho carrinho = carrinhoService.obterOuCriarCarrinho(session);
        Cliente cliente = carrinho.getCliente();

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        // Gerando um número sequencial para o pedido
        Long numeroPedido = gerarNumeroSequencialPedido();
        pedido.setNumeroPedido(numeroPedido);

        List<ProdutoPedido> itensPedido = carrinho.getProdutos().entrySet().stream()
                .map(entry -> {
                    Produto produto = produtoService.getProdutoById(entry.getKey()).orElse(null);
                    if (produto != null) {
                        ProdutoPedido item = new ProdutoPedido();
                        item.setProduto(produto);
                        item.setQuantidade(entry.getValue());
                        return item;
                    }
                    return null;
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());

        pedido.setProdutos(itensPedido);
        pedido.setValorTotal(carrinho.getTotal());
        pedido.setValorFrete(20.0); // Valor do frete fixo por enquanto
        pedido.setEnderecoEntrega(cliente.getEnderecoFaturamento());
        pedido.setFormaPagamento("Cartão de Crédito"); // Temporário, pode ser customizado

        // Salvar o pedido
        pedidoRepository.save(pedido);

        // Limpar o carrinho após finalizar o pedido
        carrinhoService.limparCarrinho(cliente, session);

        // Informar o sucesso e o número do pedido
        model.addAttribute("mensagem", "Pedido nº " + pedido.getNumeroPedido() + " realizado com sucesso!");
        model.addAttribute("pedido", pedido);
        return "resumo-pedido"; // Exibe a tela de resumo
    }

    // Método para gerar um número sequencial para o pedido
    private Long gerarNumeroSequencialPedido() {
        // Aqui você pode implementar a lógica para gerar o número sequencial
        // Pode ser uma consulta ao banco de dados ou usar um contador
        // Exemplo de incremento simples:
        Long ultimoNumeroPedido = pedidoRepository.count(); // Contando o número de pedidos no banco
        return ultimoNumeroPedido + 1;
    }



    private double calcularTotal(Map<Long, Integer> produtosCarrinho, Map<Long, Produto> produtosPorId) {
        return produtosCarrinho.entrySet()
                .stream()
                .mapToDouble(entry -> produtosPorId.get(entry.getKey()).getPreco() * entry.getValue())
                .sum();
    }
}
