package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;
import reactor.core.publisher.Mono;

/*
* TO-DO: Crear actores concretos (llamados también actores lógicos) para el escenario a resivsar. Cada actor compone la Petition
* con su Book usando zip, y simula latencia distinta.*/
public interface Actor {
    boolean supports(String type); // "LEND" o "RETURN"
    Mono<String> handle(Petition petition);

}
