package com.lhk.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * SecurityConfig
 */
@Configuration
@Order
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .oauth2Login().and()
            .logout().logoutSuccessUrl("/").and()
            .authorizeRequests(authorizeRequests ->
 				authorizeRequests
                    .antMatchers("/index.html", "/app.html", "/").permitAll()
                    .anyRequest().authenticated()
            )
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // @formatter:on
    }

    @Override
	public void configure(WebSecurity web) {
        // @formatter:off
        web
            .ignoring()
                .antMatchers("/*es2015.*");
        // @formatter:on
    }
}
