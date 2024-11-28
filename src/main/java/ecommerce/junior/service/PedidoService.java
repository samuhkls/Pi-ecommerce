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

    /**
     * Busca um pedido por ID.
     *
     * @param id ID do pedido
     * @return Pedido correspondente ou null se não encontrado
     */
    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    /**
     * Salva ou atualiza o pedido no banco de dados.
     *
     * @param pedido Pedido a ser salvo
     */
    public void salvarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("O pedido não pode ser nulo.");
        }
        pedidoRepository.save(pedido);
    }

    /**
     * Associa uma forma de pagamento ao pedido.
     *
     * @param pedido        Pedido a ser atualizado
     * @param formaPagamento Forma de pagamento a ser associada
     */
    public void associarFormaPagamento(Pedido pedido, FormaPagamento formaPagamento) {
        if (pedido == null || formaPagamento == null) {
            throw new IllegalArgumentException("Pedido ou Forma de Pagamento não podem ser nulos.");
        }
        pedido.setFormaPagamento(formaPagamento.getTipoPagamento());
        salvarPedido(pedido);
    }

    /**
     * Atualiza o status de um pedido.
     *
     * @param pedidoId  ID do pedido a ser atualizado
     * @param novoStatus Novo status para o pedido
     */
    public void atualizarStatusPedido(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = buscarPedidoPorId(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado: ID " + pedidoId);
        }
        pedido.setStatus(novoStatus);
        salvarPedido(pedido);
    }

    /**
     * Lista todos os pedidos no sistema.
     *
     * @return Lista de pedidos
     */
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    /**
     * Lista todos os pedidos ordenados por data de criação (mais recentes primeiro).
     *
     * @return Lista de pedidos ordenados por data
     */
    public List<Pedido> listarPedidosOrdenadosPorData() {
        return pedidoRepository.findAllByOrderByDataPedidoDesc();
    }

    /**
     * Lista pedidos associados a um cliente específico.
     *
     * @param clienteId ID do cliente
     * @return Lista de pedidos do cliente
     */
    public List<Pedido> listarPedidosPorCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("O ID do cliente não pode ser nulo.");
        }
        return pedidoRepository.findByClienteId(clienteId);
    }
}
