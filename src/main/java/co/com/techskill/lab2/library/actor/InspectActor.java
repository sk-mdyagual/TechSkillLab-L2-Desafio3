package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class InspectActor implements Actor {

    private final BookService bookService;

    private InspectActor(BookService bookService){
        this.bookService = bookService;
    }

    @Override
    public boolean supports(String type) {
        return "INSPECT".equals(type);
    }

    @Override
    public Mono<String> handle(Petition petition) {
        return bookService.dummyFindById(petition.getBookId())
                .delayElement(Duration.ofMillis(200))
                .map(book ->
                        String.format("[INSPECT] petition for book: %s with priority %d", book.getBookId(), petition.getPriority())
                );
    }
}
