package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import reactor.core.publisher.Mono;

public interface DummyActor {
    Mono<String> handle(PetitionDTO petition);
}
