package in.tech_camp.pictweet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/css/**", "/", "/loginForm", "/registerForm").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers("/tweets/search").permitAll()
                        .requestMatchers("/tweets/{id:[0-9]+}").permitAll()
                        .requestMatchers("/users/{id:[0-9]+}").permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginProcessingUrl("/login")
                        .loginPage("/loginForm")
                        .defaultSuccessUrl("/",true)
                        .failureUrl("/login?error")
                        .usernameParameter("email")
                        .permitAll())

                .logout(logout -> logout
                        .logoutSuccessUrl("/"));
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
