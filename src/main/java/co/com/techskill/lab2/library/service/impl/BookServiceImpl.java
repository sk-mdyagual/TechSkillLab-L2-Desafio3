package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.config.BookMapper;
import co.com.techskill.lab2.library.config.BookMapperImpl;
import co.com.techskill.lab2.library.domain.dto.BookDTO;
import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.repository.IBookRepository;
import co.com.techskill.lab2.library.service.IBookService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

 /*Operadores fundamentales
     - Operadores de transformación: map, flatMap*
     - Operadores de filtrado: filter
     - Operadores de combinación*: zip, merge, concat*/

/*
 * - map: Transforma cada elemento >> Flux.just(1, 2, 3).map(n -> n * 2); // 2, 4, 6
 * - flatMap: Usado cuando cada valor produce otro flujo asíncrono. >> Flux.just("a", "b").flatMap(this::buscarEnBD); // cada "a" y "b" produce un Mono
 * - filter: Permite pasar solo ciertos valores. >> Flux.just(1, 2, 3).filter(n -> n % 2 == 1); // 1, 3
 * - limitRate: Aplica backpressure limitando el número de elementos >> Flux.range(1, 1000).limitRate(10); // solicita 10 elementos por vez*/

    /*
    * ¿Qué es el backpressure?
    El backpressure es una técnica para regular el ritmo entre productor y consumidor.
    Si el consumidor es más lento que el productor, puede solicitar (demandar) solo lo que puede procesar.
    Evita el desbordamiento de memoria o la saturación del sistema.

    Ejemplo real:
    Un usuario ve una película a 24 fps → el servidor de streaming no envía más de 24 imágenes por segundo,
    aunque pueda generar muchas más.

    * En Project Reactor:
    - Se maneja automáticamente.
    - Se puede ajustar con operadores como limitRate().
    */

    /*Diseño de código
    * Con MongoDB clásico, escribes de forma imperativa PERO, con ReactiveMongoDB, usas operadores de flujo.
    * El código reactivo obliga a pensar en flujos de datos y composición, no en pasos secuenciales.
    * Esto implica:
    - Aprender a encadenar operadores (map, flatMap, filter, zip, etc.).
    - Manejar errores con onErrorResume o retry en lugar de try/catch.
    - Cambiar mentalidad de “traigo los datos y luego proceso” a “cuando los datos lleguen, ejecuto esta función”.*/


@Service
@Profile("mongo")
public class BookServiceImpl implements IBookService {
    private final IBookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(IBookRepository bookRepository){
        this.bookRepository = bookRepository;
        this.bookMapper = new BookMapperImpl();
    }


    @Override
    public Flux<BookDTO> findAll() {
        return bookRepository
                .findAll()
                .map(bookMapper::toDTO);
    }


    //TO-DO: Simular que una petición con type = LEND falla si el libro asociado no está disponible.
    //1. Implementar un método que busque un libro por su ID y verifique si está disponible. Al no estarlo, manejar el error con onErrorResume.
    //PREVIA: Este método también debe manejar el hecho de no encontrar el libro en primer lugar: switchIfEmpty ft .error para lanzar la excepción correspondiente.
    @Override
    public Mono<BookDTO> findById(String id) {
        return bookRepository
                .findByBookId(id)
                /*Si el Mono<Book> viene vacío, lo reemplaza por un Mono.error(...).
                Aquí sigues teniendo un Mono<Book>.*/
                .switchIfEmpty(Mono.error(new RuntimeException("Book not found or not available")))
                //Por qué aquí usamos flatMap?
                /*Usas flatMap porque dentro del bloque quieres devolver otro Mono en caso de que el libro no esté disponible (Mono.error(...)) o devolverlo (Mono.just(book)).
                Si usaras .map(...) aquí, devolverías un Mono<Mono<Book>> en el caso de Mono.error(...), lo que rompería la cadena.
                Por eso .flatMap es necesario cuando tu función retorna un Mono o Flux.*/
                .flatMap(book -> {
                    if (!book.getAvailable()) {
                        return Mono.error(new RuntimeException("Book is not available"));
                    }
                    return Mono.just(book);
                })
                /*Ahora ya tienes un Mono<Book> seguro (porque todos los caminos anteriores producen un Mono<Book> o un error).
                El mapper (bookMapper::toDTO) no devuelve un Mono, sino directamente un BookDTO.
                Como es una transformación síncrona y directa, .map es correcto aquí.*/
                .map(bookMapper::toDTO);
    }



    @Override
    public Mono<BookDTO> save(BookDTO bookDTO) {
        bookDTO.setBookId(UUID.randomUUID().toString().substring(0,10));
        bookDTO.setAvailable(true);
        return bookRepository
                .save(bookMapper.toEntity(bookDTO))
                .map(bookMapper::toDTO);

    }



}
