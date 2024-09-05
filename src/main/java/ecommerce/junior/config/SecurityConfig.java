package ecommerce.junior.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Usando o Customizer para desabilitar CSRF
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/h2-console/**").permitAll()  // Permitir acesso ao H2 Console
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")  // Custom login page URL
                        .defaultSuccessUrl("/principal", true)  // Redirect to /principal after login
                        .permitAll()
                )
                .formLogin(formLogin -> formLogin.permitAll())
                .logout(logout -> logout.permitAll())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));  // Usando o Customizer para desabilitar frameOptions

        return http.build();
    }
}
