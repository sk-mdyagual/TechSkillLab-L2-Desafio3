package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;
import reactor.core.publisher.Flux;

public interface Actor {
    boolean supports(String type);
    Flux<String> handle(Petition petition);
}
