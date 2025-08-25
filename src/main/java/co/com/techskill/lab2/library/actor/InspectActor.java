package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.dummy.PetitionService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class InspectActor implements DummyActor{

    private final PetitionService petitionService;

    public InspectActor(PetitionService petitionService) {
        this.petitionService = petitionService;
    }

    @Override
    public Mono<String> handle(PetitionDTO petition) {
        return petitionService.dummyFindAll()
                .filter(petitionDTO -> petitionDTO.getPriority()>=7)
                .delayElements(Duration.ofMillis(300))
                .map(p->
                        String.format("[INSPECT] petition for book: %s with priority %d",p.getBookId(), p.getPriority())
                ).next();
    }
}

