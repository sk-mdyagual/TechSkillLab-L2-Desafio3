package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ReturnActor implements Actor {

    @Override
    public boolean supports(String type) {
        return "RETURN".equalsIgnoreCase(type);
    }

    @Override
    public Flux<String> handle(Petition petition) {
        return Mono.just("RETURN OK -> id=%s priority=%d"
                        .formatted(petition.getPetitionId(), petition.getPriority()))
                   .delayElement(Duration.ofMillis(80))
                   .flux();
    }
}

