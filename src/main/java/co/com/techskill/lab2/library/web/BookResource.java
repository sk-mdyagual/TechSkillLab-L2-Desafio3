package co.com.techskill.lab2.library.web;

import co.com.techskill.lab2.library.domain.dto.BookDTO;
import co.com.techskill.lab2.library.service.IBookService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Profile("mongo")
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



}
