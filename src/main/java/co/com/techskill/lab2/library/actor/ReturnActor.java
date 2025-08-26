package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.service.dummy.BookService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ReturnActor implements Actor {

    private final BookService bookService;

    private ReturnActor(BookService bookService){
        this.bookService = bookService;
    }

    @Override
    public boolean supports(String type) {
        return "RETURN".equals(type);
    }

    @Override
    public Mono<String> handle(Petition petition) {
        return Mono.zip(
                        Mono.just(petition),
                        bookService.dummyFindById(petition.getBookId()))
                .delayElement(Duration.ofMillis(100))
                .map(t ->
                        String.format("[RETURN] petition for book: %s with priority %d",t.getT2().getBookId(), t.getT1().getPriority())
                );
    }
}
