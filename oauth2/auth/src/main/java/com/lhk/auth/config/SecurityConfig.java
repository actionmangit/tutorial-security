package com.lhk.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * SecurityConfig
 */
@Configuration
@Order
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.httpBasic().and()
            .logout().and()
            .authorizeRequests()
                .antMatchers("/oauth/authorize", "/login").permitAll()
                .anyRequest().authenticated();
        // @formatter:on
    }
}
