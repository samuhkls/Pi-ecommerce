package ecommerce.junior;

import ecommerce.junior.model.Endereco;
import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.Produto;
import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class JuniorApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(JuniorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user = new User();
		user.setNome("muu liro");
		user.setCpf("12345678909");
		user.setEmail("liro@penris.com");
		user.setSenha("password");
		user.setTipo(Grupo.ADMINISTRADOR);

		// Definindo o endereço de faturamento
		Endereco enderecoFaturamento = new Endereco();
		enderecoFaturamento.setCep("04469-103");
		enderecoFaturamento.setLogradouro("Rua do Comércio");
		enderecoFaturamento.setLocalidade("São Paulo");
		enderecoFaturamento.setNumero("4");
		enderecoFaturamento.setBairro("Pedreira");
		enderecoFaturamento.setUf("SP");;
		enderecoFaturamento.setComplemento("test");;
		System.out.println(enderecoFaturamento);
		user.setEnderecoFaturamento(enderecoFaturamento);

		// Adicionando um endereço de entrega
		Endereco enderecoEntrega = new Endereco();
		enderecoEntrega.setCep("04469-103");
		enderecoEntrega.setLogradouro("Rua do Comércio");
		enderecoEntrega.setLocalidade("São Paulo");
		enderecoEntrega.setNumero("4");
		enderecoEntrega.setBairro("Pedreira");
		enderecoEntrega.setUf("SP");
		enderecoEntrega.setComplemento("test");;
		System.out.println(enderecoEntrega);

		user.setEnderecosEntrega(List.of(enderecoEntrega)); // Usando List.of para criar uma lista imutável
		System.out.println(user.getEnderecosEntrega() +"enderecos de entrega lista --");
		userService.createUser(user, "password");

		User user1 = new User();
		user1.setNome("primo vieira");
		user1.setCpf("44921676844");
		user1.setEmail("vieira@primo.com");
		user1.setSenha("password");
		user1.setTipo(Grupo.ESTOQUISTA);

		// Definindo o endereço de faturamento para o segundo usuário
		Endereco enderecoFaturamento1 = new Endereco();
		enderecoFaturamento1.setCep("04469-103");
		enderecoFaturamento1.setLogradouro("Rua do Comércio");
		enderecoFaturamento1.setLocalidade("São Paulo");
		enderecoFaturamento1.setNumero("4");
		enderecoFaturamento1.setBairro("Pedreira");
		enderecoFaturamento1.setUf("SP");
		enderecoFaturamento.setComplemento("test");;

		System.out.println(enderecoFaturamento1);
		user1.setEnderecoFaturamento(enderecoFaturamento1);

		// Adicionando um endereço de entrega para o segundo usuário
		Endereco enderecoEntrega1 = new Endereco();
		enderecoEntrega1.setCep("04469-103");
		enderecoEntrega1.setLogradouro("Rua do Comércio");
		enderecoEntrega1.setLocalidade("São Paulo");
		enderecoEntrega1.setNumero("4");
		enderecoEntrega1.setBairro("Pedreira");
		enderecoEntrega1.setUf("SP");
		enderecoEntrega1.setComplemento("test");;

		System.out.println(enderecoEntrega1);
		user1.setEnderecosEntrega(List.of(enderecoEntrega1)); // Usando List.of para criar uma lista imutável

		userService.createUser(user1, "password");
	}

}
