package co.com.techskill.lab2.library.service;

import co.com.techskill.lab2.library.domain.dto.BookDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBookServiceDummy {
    //Componentes principales: Mono y Flux
    Flux<BookDTO> dummyFindAll();
    Mono<BookDTO> dummyFindById(String id);
}
