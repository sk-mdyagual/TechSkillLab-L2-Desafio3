package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;
import reactor.core.publisher.Mono;

/*
* TO-DO: Crear actores concretos (llamados también actores lógicos).*/
public interface Actor {
    boolean supports(String type); // "LEND" , "RETURN" o "INSPECT"
    Mono<String> handle(Petition petition);

}
