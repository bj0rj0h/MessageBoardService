package se.bjorjoh.controllers;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("se.bjorjoh.services")
@ComponentScan("se.bjorjoh.models")
@ComponentScan("se.bjorjoh.repositories")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


}