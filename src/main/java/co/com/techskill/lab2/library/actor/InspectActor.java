package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.service.dummy.BookService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class InspectActor implements Actor {
    private final BookService bookService;

    public InspectActor(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public boolean supports(String s) {
        return Boolean.FALSE;
    }

    public boolean byPriority(int priority) {
        return priority >= 7;

    }

    @Override
    public Mono<String> handle(PetitionDTO petition) {
        return Mono.zip(
                        Mono.just(petition),
                        bookService.dummyFindById(petition.getBookId()))
                .delayElement(Duration.ofMillis(300))
                .map(t ->
                        String.format("petition with id %" + t.getT1().getPetitionId() + " have book id %" + t.getT2().getBookId())
                );
    }
}
