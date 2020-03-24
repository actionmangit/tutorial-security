package com.lhk.auth;

import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableAuthorizationServer
public class AuthApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
    }
}
