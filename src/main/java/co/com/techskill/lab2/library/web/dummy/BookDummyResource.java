package co.com.techskill.lab2.library.web.dummy;

import co.com.techskill.lab2.library.domain.dto.BookDTO;
import co.com.techskill.lab2.library.service.dummy.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/dummy/books")
public class BookDummyResource {
    private final BookService bookService;

    public BookDummyResource(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public Flux<BookDTO> getAllBooks(){
        return bookService.dummyFindAll();
    }

    @PostMapping("/id")
    public Mono<ResponseEntity<BookDTO>> findById(@RequestBody BookDTO bookDTO){
        return bookService.dummyFindById(bookDTO.getBookId())
                .map(ResponseEntity::ok);
    }

}
