package ecommerce.junior.service;

import ecommerce.junior.model.Imagem;
import ecommerce.junior.repository.ImagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImagemService {

    private final ImagemRepository imagemRepository;

    public ImagemService(ImagemRepository imagemRepository) {
        this.imagemRepository = imagemRepository;
    }

    public List<Imagem> getAllImagens() {
        return imagemRepository.findAll();
    }

    public List<Imagem> getImagensByProdutoId(Long produtoId) {
        return imagemRepository.findByProdutoId(produtoId);
    }
}
