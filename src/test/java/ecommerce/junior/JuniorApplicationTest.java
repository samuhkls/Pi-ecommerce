package ecommerce.junior;

import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class JuniorApplicationTest {

    @BeforeEach
    void setUp() {
        User sexo = new User();

        sexo.setTipo(Grupo.ADMINISTRADOR);
        System.out.println(sexo.getTipo());
    }

    @AfterEach
    void tearDown() {
    }


}