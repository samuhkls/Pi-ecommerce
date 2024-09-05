package ecommerce.junior.service;

import ecommerce.junior.model.MyUserPrincipal;
import ecommerce.junior.model.User;
import ecommerce.junior.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Procurar o usuário pelo email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
        }
        System.out.println("Usuário encontrado: " + user.getEmail());
        System.out.println("CHAMO");
        System.out.println("CHAMO");
        System.out.println("CHAMO");
        System.out.println("CHAMO");
        System.out.println("CHAMO");
        System.out.println("CHAMO");

        return new MyUserPrincipal(user);
    }
}
