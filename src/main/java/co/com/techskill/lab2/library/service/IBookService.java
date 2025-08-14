package co.com.techskill.lab2.library.service;

import co.com.techskill.lab2.library.domain.dto.BookDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBookService {
    //Componentes principales: Mono y Flux
    Flux<BookDTO> findAll();
    Mono<BookDTO> findById(String id);
    Mono<BookDTO> save(BookDTO bookDTO);


}
