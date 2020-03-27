package com.lhk.resource;

import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;

@SpringBootApplication
@RestController
@EnableResourceServer
public class ResourceApplication {

    @RequestMapping("/")
    public Message home() {
        return new Message("Hello World");
    }

	public static void main(String[] args) {
		SpringApplication.run(ResourceApplication.class, args);
    }
    
    @Setter
    @Getter
    class Message {
        private String id = UUID.randomUUID().toString();
        private String content;
        
        public Message(String content) {
          this.content = content;
        }
    }
}
