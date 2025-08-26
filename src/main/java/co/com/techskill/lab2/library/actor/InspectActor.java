package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Profile({"default","dummy"})
public class InspectActor implements Actor {
    @Override
    public boolean supports(String type) {
        return "INSPECT".equalsIgnoreCase(type);
    }

    @Override
    public Mono<String> handle(Petition petition) {
        return Mono.defer(() -> {
                    if (petition.getPriority() < 7) {
                        return Mono.error(new IllegalArgumentException("priority < 7"));
                    }
                    if (ThreadLocalRandom.current().nextInt(6) == 0) {
                        return Mono.error(new IllegalStateException("scanner offline"));
                    }
                    String ok = String.format(
                            "[INSPECT OK] petition=%s | book=%s | priority=%d",
                            petition.getPetitionId(), petition.getBookId(), petition.getPriority()
                    );
                    return Mono.just(ok);
                })
                .delayElement(Duration.ofMillis(ThreadLocalRandom.current().nextInt(120, 450)))
                .onErrorResume(ex ->
                        Mono.just(String.format(
                                "[INSPECT ERROR] petition=%s (%s)",
                                petition.getPetitionId(), ex.getMessage()
                        ))
                );
    }
}