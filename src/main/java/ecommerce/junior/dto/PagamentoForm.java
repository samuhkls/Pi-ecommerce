package ecommerce.junior.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PagamentoForm {

    @NotBlank(message = "O tipo de pagamento é obrigatório.")
    private String tipoPagamento;  // Exemplo: 'cartao', 'boleto', 'pix', etc.


    // Getters e Setters
    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

}
