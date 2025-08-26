package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.repository.IBookRepository;
import co.com.techskill.lab2.library.service.dummy.BookService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class LendActor implements Actor {

    private final BookService bookRepository;

    public LendActor(BookService bookRepository){
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean supports(String type) {
        return "LEND".equals(type);
    }

    @Override
    public Mono<String> handle(PetitionDTO petition) {
        return Mono.zip(
                Mono.just(petition),
                bookRepository.dummyFindById(petition.getBookId()))
                .delayElement(Duration.ofMillis(300))
                .map(t ->
                   String.format("[LEND] petition for book: %s with priority %d",t.getT2().getBookId(), t.getT1().getPriority())
                );
    }
}
