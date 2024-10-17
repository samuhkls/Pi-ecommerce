package ecommerce.junior;

import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.Produto;
import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
		user.setNome("muu rilo");
		user.setCpf("12345678909");
		user.setEmail("liro@penris.com");
		user.setSenha("password");
		user.setEnderecoEntrega("01311-000, Avenida Paulista, 1000, Sala 202, Bela Vista, São Paulo, SP");
		user.setEnderecoFaturamento("01311-000, Avenida Paulista, 1000, Sala 202, Bela Vista, São Paulo, SP");
		user.setTipo(Grupo.ADMINISTRADOR);

		User user1 = new User();
		user1.setNome("primo vieira");
		user1.setCpf("44921676844");
		user1.setEmail("vieira@primo.com");
		user1.setSenha("password");
		user1.setEnderecoEntrega("01311-000, Avenida Paulista, 1000, Sala 202, Bela Vista, São Paulo, SP");
		user1.setEnderecoFaturamento("01311-000, Avenida Paulista, 1000, Sala 202, Bela Vista, São Paulo, SP");
		user1.setTipo(Grupo.ESTOQUISTA);

		userService.createUser(user, "password");
		userService.createUser(user1, "password");
	}
}
