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

    public void deletarImagem(Imagem imagem) {
        imagemRepository.delete(imagem);
    }


    public List<Imagem> getAllImagens() {
        return imagemRepository.findAll();
    }

    public List<Imagem> getImagensByProdutoId(Long produtoId) {
        return imagemRepository.findByProdutoId(produtoId);
    }

    public Imagem getImagemByProdutoId(Long produtoId) {
        // Aqui você faz a consulta no banco para pegar a imagem do produto.
        // Se for o caso de ter mais de uma imagem, você pode escolher a primeira ou a mais relevante.
        return imagemRepository.findFirstByProdutoId(produtoId);
    }

}