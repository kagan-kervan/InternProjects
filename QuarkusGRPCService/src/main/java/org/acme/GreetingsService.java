package org.acme;


import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingsService {
    public String greeting(String name){
        return "hello " + name;
    }
}
