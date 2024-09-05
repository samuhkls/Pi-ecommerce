package ecommerce.junior;

import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.User;
import ecommerce.junior.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JuniorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(JuniorApplication.class);
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

		userService.createUser(user, "password");
		System.out.println("teste");
	}
}
