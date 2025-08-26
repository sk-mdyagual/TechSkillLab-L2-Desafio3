package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.repository.IBookRepository;
import co.com.techskill.lab2.library.service.dummy.PetitionService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class InspectActor implements Actor{

    private final IBookRepository bookRepository;
    private PetitionService petitionService;
    private InspectActor(IBookRepository bookRepository, PetitionService petitionService){

        this.bookRepository = bookRepository;
        this.petitionService = petitionService;
    }

    @Override
    public boolean supports(String type) {
        return "INSPECT".equals(type);
    }

    @Override
    public Mono<String> handle(Petition petition) {
        return Mono.zip(
                        Mono.just(petition),
                        petitionService.dummyFindById(petition.getPetitionId()))
                .delayElement(Duration.ofMillis(300))
                .map(t ->
                        String.format("[INSPECT] petition for book: %s with priority %d",t.getT2().getBookId(), t.getT1().getPriority())
                );
    }
}
