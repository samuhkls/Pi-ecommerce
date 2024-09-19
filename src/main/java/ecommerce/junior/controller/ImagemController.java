package ecommerce.junior.controller;

import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@RestController
@RequestMapping("/imagens")
public class ImagemController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Resource> obterImagem(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<Produto> produtoOptional = produtoRepository.findById(id);
        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();
            byte[] imagem = produto.getImagem();
            if (imagem != null && imagem.length > 0) {
                InputStream inputStream = new ByteArrayInputStream(imagem);
                InputStreamResource resource = new InputStreamResource(inputStream);

                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_TYPE, "image/jpeg");  // Defina o tipo MIME correto
                headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(imagem.length));

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            }
        }
        return ResponseEntity.notFound().build();
    }
}
