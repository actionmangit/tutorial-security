package com.lhk.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class BackApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
    }
}
