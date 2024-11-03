package ecommerce.junior.service;

import ecommerce.junior.model.Endereco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class EnderecoService {

    @Autowired
    private static RestTemplate restTemplate;

    public static boolean validarCep(String cep) {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
            Endereco endereco = restTemplate.getForObject(url, Endereco.class);
            return endereco != null && "OK".equals(endereco.getCep());
        } catch (HttpClientErrorException e) {
            return false; // se der erro, o CEP é inválido
        }
    }
}
