package ecommerce.junior.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Carrinho carrinho;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_faturamento_id", nullable = false)
    private Endereco enderecoFaturamento;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Endereco> enderecosEntrega;


    public Cliente() {
    }

    public Cliente(Endereco enderecoFaturamento, List<Endereco> enderecosEntrega) {
        this.enderecoFaturamento = enderecoFaturamento;
        this.enderecosEntrega = enderecosEntrega;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Carrinho getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
    }

    public Endereco getEnderecoFaturamento() {
        return enderecoFaturamento;
    }

    public void setEnderecoFaturamento(Endereco enderecoFaturamento) {
        this.enderecoFaturamento = enderecoFaturamento;
    }

    public List<Endereco> getEnderecosEntrega() {
        return enderecosEntrega;
    }

    public void setEnderecosEntrega(List<Endereco> enderecosEntrega) {
        this.enderecosEntrega = enderecosEntrega;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", enderecoFaturamento=" + enderecoFaturamento +
                ", enderecosEntrega=" + enderecosEntrega +
                '}';
    }
}
