package ecommerce.junior.dto;

import java.util.Map;

public class CarrinhoDTO {

    private Map<Long, Integer> produtos; // ID do produto e quantidade
    private double total;
    private double frete;

    public Map<Long,Integer> getProdutos() {
        return produtos;
    }

    public void setProdutos(Map<Long,Integer> produtos) {
        this.produtos = produtos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getFrete() {
        return frete;
    }

    public void setFrete(double frete) {
        this.frete = frete;
    }
}
