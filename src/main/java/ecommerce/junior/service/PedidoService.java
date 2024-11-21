package ecommerce.junior.service;

import ecommerce.junior.model.Pedido;
import ecommerce.junior.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public void salvarPedido(Pedido pedido) {
        pedidoRepository.save(pedido);
    }
}

