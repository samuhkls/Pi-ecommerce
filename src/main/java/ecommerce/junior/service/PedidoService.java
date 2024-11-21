package ecommerce.junior.service;

import ecommerce.junior.model.FormaPagamento;
import ecommerce.junior.model.Pedido;
import ecommerce.junior.model.StatusPedido;
import ecommerce.junior.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Método para buscar o pedido por ID
    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    // Método para salvar ou atualizar o pedido no banco de dados
    public void salvarPedido(Pedido pedido) {
        pedidoRepository.save(pedido);
    }

    // Método para atualizar a forma de pagamento do pedido
    public void associarFormaPagamento(Pedido pedido, FormaPagamento formaPagamento) {
        if (pedido != null && formaPagamento != null) {
            pedido.setFormaPagamento(formaPagamento.getTipoPagamento());
            salvarPedido(pedido); // Salvando o pedido com a forma de pagamento associada
        } else {
            throw new IllegalArgumentException("Pedido ou Forma de Pagamento não podem ser nulos");
        }
    }

    public void atualizarStatusPedido(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = buscarPedidoPorId(pedidoId);
        if (pedido != null) {
            pedido.setStatus(novoStatus);
            salvarPedido(pedido);
        } else {
            throw new IllegalArgumentException("Pedido não encontrado: ID " + pedidoId);
        }
    }

    // Listar todos os pedidos
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPedidosOrdenadosPorData() {
        return pedidoRepository.findAllByOrderByDataPedidoDesc();
    }

    // Listar pedidos por cliente
    public List<Pedido> listarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

}
