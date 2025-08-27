package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.service.dummyServices.PetitionDummyServices;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class InspectActor implements DummyActor{

    private final PetitionDummyServices PetitionDummyServices;

    public InspectActor(PetitionDummyServices PetitionDummyServices) {
        this.PetitionDummyServices = PetitionDummyServices;
    }

    @Override
    public Mono<String> handle(PetitionDTO petition) {
        return PetitionDummyServices.dummyFindAll()
                .filter(petitionDTO -> petitionDTO.getPriority()>=7)
                .delayElements(Duration.ofMillis(500))
                .map(p-> String.format("[INSPECT] petition for book: %s with priority %d",p.getBookId(), p.getPriority()))
                .next();
    }
}

