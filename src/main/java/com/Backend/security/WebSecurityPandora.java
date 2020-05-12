package com.Backend.security;

import com.Backend.repository.IUserRepo;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.Backend.security.SecurityConstants.*;

@EnableWebSecurity
@Configuration
class WebSecurityPandora extends WebSecurityConfigurerAdapter {

    @Autowired
    IUserRepo repository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .addFilterAfter(new JWTAuthorizationFilter(repository), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, ESTADISTICAS).permitAll()
                .antMatchers(HttpMethod.POST, REGISTRO_USUARIO_URL).permitAll()
                .antMatchers(HttpMethod.POST,LOGIN_USUARIO_URL).permitAll()
                //Solo puede quedar la de abajo
                .antMatchers(HttpMethod.POST,LOGIN_USUARIO2FA_URL).permitAll()
                .antMatchers(HttpMethod.POST,LOGIN_2FA_URL).permitAll()
                .antMatchers(HttpMethod.GET, ROBUSTEZ).permitAll()
                .antMatchers(HttpMethod.GET,CONSULTAR_TODOS_USUARIOS_URL).permitAll()
                .antMatchers(HttpMethod.GET,VERIFICAR_USUARIO_URL).permitAll()
                .antMatchers(HttpMethod.POST,CONTACTO_URL).permitAll()
                .anyRequest().authenticated();
    }
    
    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}