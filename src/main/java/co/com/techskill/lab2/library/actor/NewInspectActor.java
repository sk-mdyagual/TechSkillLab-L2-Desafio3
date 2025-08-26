package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.service.IBookServiceDummy;
import reactor.core.publisher.Mono;

public class NewInspectActor implements Actor {

    private final IBookServiceDummy bookRepository;

    public NewInspectActor(IBookServiceDummy bookRepository) {
        this.bookRepository = bookRepository;
    }


    @Override
    public boolean supports(String type) {
        return "INSPECT".equalsIgnoreCase(type);
    }

    @Override
    public Mono<String> handle(PetitionDTO petition) {
        return Mono.zip(
                        Mono.just(petition),
                        bookRepository.dummyFindById(petition.getBookId()))
                .delayElement(java.time.Duration.ofSeconds(2))
                .map(t ->
                        String.format("[INSPECT] petition for book: %s with priority %d", t.getT2().getBookId(), t.getT1().getPriority())
                );
    }
}
