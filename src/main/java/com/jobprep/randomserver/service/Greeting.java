package com.jobprep.randomserver.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Greeting {

    @GetMapping(value = "/greeting")
    public String greet(){
        return "Hello";
    }
}
