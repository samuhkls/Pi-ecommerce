package ecommerce.junior.controller;

import ecommerce.junior.model.Endereco;
import ecommerce.junior.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping("/buscar-endereco")
    public Endereco buscarEndereco(@RequestParam String cep) {
        try {
            String url = "https://viacep.com.br/ws/" + cep + "/json/";
            return enderecoService.buscarEndereco(cep);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}