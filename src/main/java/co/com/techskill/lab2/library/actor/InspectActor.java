package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;


import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class InspectActor implements Actor {

    @Override
    public boolean supports(String type) {
        return "INSPECT".equalsIgnoreCase(type);
    }

    @Override
    public Flux<String> handle(Petition petition) {
        // Simulación: aleatoriamente falla ~10% y agrega latencia 120–400 ms
        int latency = ThreadLocalRandom.current().nextInt(120, 401);
        boolean fail = ThreadLocalRandom.current().nextInt(100) < 10;

        Mono<String> task = Mono.defer(() -> {
                    if (fail) return Mono.error(new RuntimeException("Falla simulada INSPECT"));
                    return Mono.just("INSPECT OK -> id=%s priority=%d (t=%dms)"
                            .formatted(petition.getPetitionId(), petition.getPriority(), latency));
                })
                .delayElement(Duration.ofMillis(latency));

        return task.flux();
    }
}
