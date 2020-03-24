package com.lhk.resource.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * SecurityConfig
 */
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
	protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
			.authorizeRequests()
				.anyRequest().authenticated();
	}
}