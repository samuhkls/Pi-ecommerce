package ecommerce.junior.service;

import ecommerce.junior.model.Imagem;
import ecommerce.junior.model.Produto;
import ecommerce.junior.repository.ImagemRepository;
import ecommerce.junior.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImagemService {

    @Autowired
    private ImagemRepository imagemRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private final Path rootLocation = Paths.get("caminho/para/o/diretório/de/imagens");

    public Imagem saveImagem(MultipartFile file, Long produtoId) throws IOException {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destinationFile = this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();

        Files.copy(file.getInputStream(), destinationFile);

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        Imagem imagem = new Imagem();
        imagem.setCaminho(destinationFile.toString());
        imagem.setPrincipal(false);
        imagem.setProduto(produto);

        return imagemRepository.save(imagem);
    }

    public void definirImagemPrincipal(Long produtoId, Long imagemId) {
        imagemRepository.updatePrincipalStatus(false, produtoId);

        Imagem imagem = imagemRepository.findById(imagemId)
                .orElseThrow(() -> new IllegalArgumentException("Imagem não encontrada."));
        imagem.setPrincipal(true);
        imagemRepository.save(imagem);
    }
}
