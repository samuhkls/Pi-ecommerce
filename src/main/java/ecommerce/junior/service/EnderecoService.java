package ecommerce.junior.service;

import ecommerce.junior.model.Cliente;
import ecommerce.junior.model.Endereco;
import ecommerce.junior.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
public class EnderecoService {

    @Autowired
    private static RestTemplate restTemplate;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    public EnderecoService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    public static boolean validarCep(String cep) {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
            // Aqui você deve verificar se o retorno da API é um endereço válido
            Endereco endereco = restTemplate.getForObject(url, Endereco.class);

            // verifica se o endereco não é nulo e se o campo cep não é nulo
            return endereco != null && endereco.getCep() != null && endereco.getCep().equals(cep);
        } catch (HttpClientErrorException e) {
            return false; // se der erro, o CEP é inválido
        }

    }
    public Endereco buscarEndereco(String cep) {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        try {
            return restTemplate.getForObject(url, Endereco.class);
        } catch (Exception e) {
            return null; // Retorna nulo em caso de erro
        }
    }

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public List<Endereco> listarEnderecosPorCliente(Cliente cliente) {
        return enderecoRepository.findByCliente(cliente);
    }

    public Endereco salvarEndereco(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public void deletarEndereco(Long id) {
        enderecoRepository.deleteById(id);
}}

