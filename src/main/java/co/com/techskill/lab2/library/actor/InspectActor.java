package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.service.dummy.BookService;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class InspectActor implements Actor{
    private final BookService bookRepository;

    public InspectActor(BookService bookRepository) {
        this.bookRepository = bookRepository;
    }


    @Override
    public boolean supports(String priority) {
        return "INSPECT".equals(priority);
    }


    @Override
    public Mono<String> handle(PetitionDTO petition) {
        return Mono.zip(
                        Mono.just(petition),
                        bookRepository.dummyFindById(petition.getBookId()))
                .map(t ->
                        String.format("[INSPECT] Petition for book: %s with priority %d",t.getT2().getBookId(), t.getT1().getPriority())
                );
    }

}
