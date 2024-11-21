package ecommerce.junior.service;

import ecommerce.junior.dto.PagamentoForm;
import ecommerce.junior.model.FormaPagamento;
import ecommerce.junior.model.Pedido;
import ecommerce.junior.repository.FormaPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    @Autowired
    private static FormaPagamentoRepository formaDePagamentoRepository;

    public FormaPagamento fromDTO(PagamentoForm formaDePagamentoForm, Pedido pedido) {
        FormaPagamento formaDePagamento = new FormaPagamento();
        formaDePagamento.setTipoPagamento(formaDePagamentoForm.getTipoPagamento());
        formaDePagamento.setPedido(pedido);

        return formaDePagamento;
    }

    public static FormaPagamento buscarFormaDePagamentoPorId(Long id) {
        return formaDePagamentoRepository.findById(id).orElse(null); // Retorna null se não encontrar
    }

    public FormaPagamento salvar(FormaPagamento formaDePagamento) {
        return formaDePagamentoRepository.save(formaDePagamento);
    }
}
