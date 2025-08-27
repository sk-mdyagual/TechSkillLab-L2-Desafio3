package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.service.dummy.BookService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InspectActor implements Actor {
    private final BookService bookService;

    public InspectActor(BookService bookServiceb) {
        this.bookService = bookServiceb;
    }

    @Override
    public boolean supports(String type) {
        return "INSPECT".equals(type);
    }

    @Override
    public Mono<String> handle(PetitionDTO petition) {
        return Mono.zip(
                        Mono.just(petition),
                        bookService.findByBookId(petition.getBookId()))
                .map(t ->
                        String.format("[INSPECT] petition for book: %s with priority %d", t.getT2().getBookId(), t.getT1().getPriority())
                );
    }
}
