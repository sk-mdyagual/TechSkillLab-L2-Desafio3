package co.com.techskill.lab2.library.web;

import co.com.techskill.lab2.library.domain.dto.BookDTO;
import co.com.techskill.lab2.library.service.IBookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/books")
public class BookResource {
    private final IBookService bookService;

    public BookResource(IBookService bookService){
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public Flux<BookDTO> getAllBooks(){
        return bookService.findAll();
    }

    @PostMapping("/id")
    public Mono<ResponseEntity<BookDTO>> findById(@RequestBody BookDTO bookDTO){
        return bookService.findById(bookDTO.getBookId())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/save")
    public Mono<ResponseEntity<BookDTO>> saveBook(@RequestBody BookDTO bookDTO){
        return bookService.save(bookDTO)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/create-sample")
    public Flux<ResponseEntity<BookDTO>> createSampleBooks() {
        BookDTO book1 = new BookDTO("The Art of Programming", "978-0201896831", 10);
        book1.setBookId("BOOK001");
        
        BookDTO book2 = new BookDTO("Reactive Programming", "978-1617295867", 5);
        book2.setBookId("BOOK002");
        
        BookDTO book3 = new BookDTO("Spring Boot in Action", "978-1617292545", 8);
        book3.setBookId("BOOK003");
        
        BookDTO book4 = new BookDTO("Microservices Patterns", "978-1617294549", 12);
        book4.setBookId("BOOK004");

        return Flux.just(book1, book2, book3, book4)
                .flatMap(bookService::save)
                .map(ResponseEntity::ok);
    }



}
