package ecommerce.junior;

import ecommerce.junior.model.*;
import ecommerce.junior.service.ProdutoService;
import ecommerce.junior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class JuniorApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	@Autowired
	private ProdutoService produtoService;

	public static void main(String[] args) {
		SpringApplication.run(JuniorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		criarUsuarios();
		criarProdutos();



	}

	private void criarUsuarios() {

		User admin = new User();
		admin.setNome("Murilo Liro");
		admin.setCpf("12345678909");
		admin.setEmail("liro@penris.com");
		admin.setSenha("password");
		admin.setTipo(Grupo.ADMINISTRADOR);

		Endereco enderecoAdmin = new Endereco();
		enderecoAdmin.setCep("04469-103");
		enderecoAdmin.setLogradouro("Rua do Comércio");
		enderecoAdmin.setLocalidade("São Paulo");
		enderecoAdmin.setNumero("4");
		enderecoAdmin.setBairro("Pedreira");
		enderecoAdmin.setUf("SP");
		enderecoAdmin.setComplemento("Apto 101");

		userService.createUser(admin, "password");

		// Usuário Estoquista
		User estoquista = new User();
		estoquista.setNome("Primo Vieira");
		estoquista.setCpf("44921676844");
		estoquista.setEmail("vieira@primo.com");
		estoquista.setSenha("password");
		estoquista.setTipo(Grupo.ESTOQUISTA);

		Endereco enderecoEstoquista = new Endereco();
		enderecoEstoquista.setCep("04469-104");
		enderecoEstoquista.setLogradouro("Rua da Liberdade");
		enderecoEstoquista.setLocalidade("São Paulo");
		enderecoEstoquista.setNumero("45");
		enderecoEstoquista.setBairro("Centro");
		enderecoEstoquista.setUf("SP");
		enderecoEstoquista.setComplemento("Sala 302");

		userService.createUser(estoquista, "password");

		System.out.println("Usuários criados com sucesso!");
	}

	private void criarProdutos() {
		List<Produto> produtos = new ArrayList<>();

		produtos.add(criarProduto("Teclado Mecânico HyperX Alloy FPS Pro", 299.99, 50,
				"Teclado mecânico compacto para jogos, switches Cherry MX Red, retroiluminação LED vermelha.",
				true,
				List.of("uploads/teclado1.jpg", "uploads/teclado1-back.jpg")));

		produtos.add(criarProduto("Mouse Gamer Razer DeathAdder V2", 399.99, 30,
				"Mouse gamer ergonômico com sensor óptico de 20.000 DPI e iluminação RGB.",
				true,
				List.of("uploads/mouse1.jpg", "uploads/mouse1-side.jpg")));

		produtos.add(criarProduto("Headset Logitech G935", 599.99, 20,
				"Headset sem fio com som surround 7.1 e iluminação RGB LightSync.",
				true,
				List.of("uploads/headset1.jpg", "uploads/headset1-lights.jpg")));

		produtos.add(criarProduto("Monitor Gamer AOC Hero 27''", 1299.99, 10,
				"Monitor com taxa de atualização de 144Hz, 1ms de resposta e resolução Full HD.",
				true,
				List.of("uploads/monitor1.jpg", "uploads/monitor1-stand.jpg")));

		produtos.add(criarProduto("Cadeira Gamer DXRacer", 1599.99, 5,
				"Cadeira ergonômica com ajustes de altura, reclinação e apoio lombar.",
				true,
				List.of("uploads/cadeira1.jpg", "uploads/cadeira1-side.jpg")));

		produtos.add(criarProduto("Mousepad Gamer SteelSeries QcK", 99.99, 100,
				"Mousepad de tecido com superfície otimizada para precisão e controle.",
				true,
				List.of("uploads/mousepad1.jpg")));

		produtos.add(criarProduto("Gabinete Gamer NZXT H510", 599.99, 15,
				"Gabinete minimalista com painel lateral de vidro temperado.",
				true,
				List.of("uploads/gabinete1.jpg", "uploads/gabinete1-open.jpg")));

		produtos.add(criarProduto("Placa de Vídeo NVIDIA GeForce RTX 3070", 4999.99, 8,
				"Placa de vídeo com 8GB GDDR6 e suporte a Ray Tracing.",
				true,
				List.of("uploads/gpu1.jpg", "uploads/gpu1-front.jpg")));

		produtos.add(criarProduto("Fonte Corsair RM750x", 699.99, 25,
				"Fonte modular de 750W com eficiência 80 Plus Gold.",
				true,
				List.of("uploads/fonte1.jpg")));

		produtos.add(criarProduto("Webcam Logitech StreamCam", 799.99, 12,
				"Webcam Full HD 1080p ideal para streaming e videoconferências.",
				true,
				List.of("uploads/webcam1.jpg")));

		produtos.forEach(produto -> {
			Produto salvo = produtoService.salvarProduto(produto);
			System.out.println("Produto salvo: " + salvo.getNome() + " (ID: " + salvo.getId() + ")");
		});

		System.out.println("Produtos criados com sucesso!");
	}

	private Produto criarProduto(String nome, double preco, int quantidade, String descricao, boolean ativo, List<String> urlsImagens) {
		Produto produto = new Produto();
		produto.setNome(nome);
		produto.setPreco(preco);
		produto.setQuantidadeEmEstoque(quantidade);
		produto.setDescricaoDetalhada(descricao);
		produto.setAtivo(ativo);

		List<Imagem> imagens = new ArrayList<>();
		for (String url : urlsImagens) {
			Imagem imagem = new Imagem();
			imagem.setUrl(url);
			imagem.setProduto(produto);
			imagens.add(imagem);
		}
		produto.setImagens(imagens);

		return produto;
	}
}
