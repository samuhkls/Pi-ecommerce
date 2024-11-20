package ecommerce.junior.controller;

import ecommerce.junior.model.Imagem;
import ecommerce.junior.service.ImagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagens")
public class ImagemController {

    private final ImagemService imagemService;

    public ImagemController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<String>> getImagensByProduto(@PathVariable Long produtoId) {
        List<Imagem> imagens = imagemService.getImagensByProdutoId(produtoId);
        List<String> urls = imagens.stream().map(Imagem::getUrl).toList();
        return ResponseEntity.ok(urls);
    }
}
