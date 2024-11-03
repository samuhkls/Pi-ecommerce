package ecommerce.junior.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "usuario")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;

    @Enumerated(EnumType.STRING)
    private Grupo tipo;

    private boolean ativo = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_faturamento_id")
    private Endereco enderecoFaturamento;

    // Endereço de Entrega
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecosEntrega;  // Lista de endereços de entrega

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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

    public Grupo getTipo() {
        return tipo;
    }

    public void setTipo(Grupo tipo) {
        this.tipo = tipo;
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
        return "User{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", tipo=" + tipo +
                ", ativo=" + ativo +
                '}';
    }
}
