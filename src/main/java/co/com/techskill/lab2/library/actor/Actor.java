package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import reactor.core.publisher.Mono;

/*
 * TO-DO: Crear actores concretos (llamados también actores lógicos).*/
public interface Actor {
    boolean supports(String type); // "LEND" o "RETURN"
    Mono<String> handle(PetitionDTO petition);

}