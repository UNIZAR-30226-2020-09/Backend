package com.Backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.Backend.security.Constants.*;

@Configuration
@EnableWebSecurity
public class WebSecurityPandora extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServicePandora userDetailsServicePandora;

    public WebSecurityPandora(UserDetailsServicePandora userDetailsServicePandora) {
        this.userDetailsServicePandora = userDetailsServicePandora;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        /*
         * 1. Se desactiva el uso de cookies
         * 2. Se activa la configuración CORS con los valores por defecto
         * 3. Se desactiva el filtro CSRF
         * 4. Se indica que el login no requiere autenticación
         * 5. Se indica que el resto de URLs esten securizadas
         */
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().and().csrf().disable().authorizeRequests()
                .antMatchers(LOGIN_URL).permitAll()
                .antMatchers(REGISTRO_URL).permitAll()
                .antMatchers(LISTAR_TODOS_URL).permitAll()
                .anyRequest().authenticated().and()
                .formLogin()
                .loginProcessingUrl(LOGIN_URL).and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()));
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Se define la clase que recupera los usuarios y el algoritmo para procesar las passwords
        auth.userDetailsService(userDetailsServicePandora).passwordEncoder(new BCryptPasswordEncoder());
    }

}