package co.com.techskill.lab2.library.service;


import reactor.core.publisher.Flux;

public interface IOrchestratorService {
    Flux<String> orchestrate();
}
