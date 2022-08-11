package com.artisan.example.springboot;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NamedStoredProcedureQueries;

@RestController
@RequestMapping("hello")
public class HelloRestController {

    @GetMapping("user")
    public String helloUser(){
        return "Hello User";
    }
    @GetMapping("admin")
    public String helloAdmin(){
        return "Hello Admin";
    }
}
