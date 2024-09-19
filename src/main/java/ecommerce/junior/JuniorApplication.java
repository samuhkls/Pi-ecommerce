package ecommerce.junior;

import ecommerce.junior.model.Grupo;
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
		user.setNome("liro");
		user.setCpf("12345678909");
		user.setEmail("liro@penris.com");
		user.setSenha("password");
		user.setTipo(Grupo.ADMINISTRADOR);

		User user1 = new User();
		user1.setNome("primovieira");
		user1.setCpf("44921676844");
		user1.setEmail("vieira@primo.com");
		user1.setSenha("password");
		user1.setTipo(Grupo.ESTOQUISTA);

		userService.createUser(user, "password");
		userService.createUser(user1, "password");
	}
}
