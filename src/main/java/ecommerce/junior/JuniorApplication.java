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
		User sexo = new User();

		sexo.setNome("breakpoint");
		sexo.setCpf("123123");
		sexo.setEmail("lucas@email");
		sexo.setSenha("senha");
		sexo.setTipo(Grupo.ESTOQUISTA);
		System.out.println(sexo.getTipo());

		//UserService service = new UserService();

		userService.createUser(sexo);

		System.out.println(sexo.getNome());
	}

}
