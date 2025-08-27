package co.com.techskill.lab2.library.Actores;

import co.com.techskill.lab2.library.domain.entity.Petition;
import reactor.core.publisher.Mono;

public interface Actores {

    boolean supports(String type); // "LEND" o "RETURN"
    Mono<String> handle(Petition petition);

}
