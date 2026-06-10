package com.HiveGroup.HiveRH.Common.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request ->
                        request.requestMatchers("/api/**")
                                .permitAll()
                                .requestMatchers("/api/license")
                                .authenticated()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    //Pasar al service del user -- testeo
    @Bean
    public UserDetailsService testUser(PasswordEncoder passwordEncoder){
        User.UserBuilder user = User.builder();
        UserDetails user1 = user.username("root")
                .password(passwordEncoder.encode("1234"))
                .roles()
                .build();
        return new InMemoryUserDetailsManager(user1);
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
