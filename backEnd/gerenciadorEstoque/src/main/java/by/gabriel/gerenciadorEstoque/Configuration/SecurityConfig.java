package by.gabriel.gerenciadorEstoque.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Libera todos os endpoints dentro de /usuarios
                .requestMatchers("/usuario/**").permitAll()

                    .requestMatchers("/movimentacao/**").permitAll()

                    .requestMatchers("/produto/**").permitAll()

                    .requestMatchers("/formPag/**").permitAll()

                    .requestMatchers("/pedido/**").permitAll()

                    .requestMatchers("/cliente/**").permitAll()

                    .requestMatchers("/servicos/**").permitAll()

                    .requestMatchers("/os/**").permitAll()

                // Qualquer outra requisição exige autenticação
                .anyRequest().authenticated()
            );

        return http.build();
    }
}

