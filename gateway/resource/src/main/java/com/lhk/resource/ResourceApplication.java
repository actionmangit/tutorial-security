package com.lhk.resource;

import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;

@SpringBootApplication
@RestController
public class ResourceApplication {

    @RequestMapping("/")
    public Message home() {
        System.out.println("들어옴");
        return new Message("Hello World");
    }

	public static void main(String[] args) {
        System.out.println("스똬뚜");
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
