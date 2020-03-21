package com.Backend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.Backend.security.Constants.*;

@EnableWebSecurity
@Configuration
class WebSecurityPandora extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, REGISTRO_USUARIO_URL).permitAll()
                .antMatchers(HttpMethod.POST,LOGIN_USUARIO_URL).permitAll()
                .antMatchers(HttpMethod.GET,CONSULTAR_TODOS_USUARIOS_URL).permitAll()
                .antMatchers(HttpMethod.POST,CONTACTO_URL).permitAll()
                .anyRequest().authenticated();
    }

}