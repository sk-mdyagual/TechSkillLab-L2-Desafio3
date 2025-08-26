package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.config.PetitionMapper;
import co.com.techskill.lab2.library.config.PetitionMapperImpl;
import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.repository.IPetitionRepository;
import co.com.techskill.lab2.library.service.IPetitionService;
import co.com.techskill.lab2.library.repository.IBookRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
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
public class PetitionServiceImpl implements IPetitionService {
    /*
     * Cambiar de MongoDB síncrono a ReactiveMongoDB no te hace las consultas más rápidas en sí,
     * pero te permite atender muchas más solicitudes con el mismo hardware, gracias a que no bloqueas hilos
     * y trabajas con un modelo de backpressure que escala mejor.
     * */
    private final IPetitionRepository petitionRepository;
    private final IBookRepository bookRepository;
    private final PetitionMapper petitionMapper;

    public PetitionServiceImpl(IPetitionRepository petitionRepository, IBookRepository bookRepository) {
        this.petitionRepository = petitionRepository;
        this.bookRepository = bookRepository;
        this.petitionMapper = new PetitionMapperImpl();
    }
    @Override
    public Flux<PetitionDTO> findALl() {
        return petitionRepository
                .findAll()
                .map(petitionMapper::toDTO);
    }

    @Override
    public Mono<PetitionDTO> findById(String id) {
        return petitionRepository
                .findByPetitionId(id)
                .map(petitionMapper::toDTO);
    }


    @Override
    public Mono<PetitionDTO> save(PetitionDTO petitionDTO) {
        petitionDTO.setPetitionId(UUID.randomUUID().toString().substring(0,10));
        petitionDTO.setSentAt(LocalDate.now());
        return petitionRepository
                .save(petitionMapper.toEntity(petitionDTO))
                .map(petitionMapper::toDTO);
    }

    //TO- DO Filter example findByPriority
    @Override
    public Flux<PetitionDTO> findByPriority(Integer p) {
        return petitionRepository.findAll()
                .filter(petition -> Objects.equals(petition.getPriority(), p))
                .map(petitionMapper::toDTO);
    }


    //Check priorities with a delay of 1 second to show up the processing
    @Override
    public Flux<String> checkPriorities(PetitionDTO petitionDTO) {
        return findByPriority(petitionDTO.getPriority())
                .map(pt -> LocalTime.now() + " - Check priority with level: " + pt.getPriority()
                        + ", Petition ID: " + pt.getPetitionId()
                        + ", For book ID: " + pt.getBookId() + "\n")
                .delayElements(Duration.ofMillis(1000))
                .doOnNext(System.out::print);
    }

    //TO-DO: Simular una petición con tipo = LEND que falla si el libro asociado no está disponible.
    @Override
    public Flux<String> processPetition(PetitionDTO petitionDTO) {
        return petitionRepository.findAll()
                .filter(p -> "LEND".equals(p.getType()))
                .flatMap(p -> bookRepository.findByBookId(p.getBookId())
                        .map(book -> "Petition " + p.getPetitionId() + " approved")
                        .onErrorResume(e -> Mono.just("Petition " + p.getPetitionId() + " failed: " + e.getMessage()))
                );
    }


    //3. Simular intermitencia y usar retry y timeout: Resilencia básica - Esto funciona bien para resiliencia básica, pero no “recuerda”
    // el historial de fallos. Cada nueva petición empieza de cero.
    /*
    * Un circuit breaker real necesita estado y memoria de fallos recientes.
    * Resilience4j para mostrar el patrón “profesional”.
    * Un Circuit Breaker mantiene un estado global (por ejemplo, usando Resilience4j o Netflix Hystrix):

        Closed → funciona ok, las peticiones pasan.

        Open → demasiados fallos recientes → bloquea llamadas por un tiempo y responde de inmediato con un fallback.

        Half-Open → después de un tiempo de espera, deja pasar algunas llamadas para probar si el servicio se recuperó.

        >> La clave es que aprende del historial de fallos y protege al sistema, no solo a una llamada puntual.*/
    @Override
    public Mono<String> simulateIntermittency(PetitionDTO petitionDTO) {
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("petitionService");
        return Mono.fromCallable(() -> petitionDTO)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .flatMap(dto -> petitionRepository.findByPetitionId(dto.getPetitionId()))
                .flatMap(petition -> {
                    if (Math.random() < 0.5) {
                        return Mono.error(new RuntimeException("Simulated intermittent failure"));
                    }
                    return Mono.just("Petition " + petition.getPetitionId() + " processed successfully");
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("Petition " + petitionDTO.getPetitionId() + " failed: " + e.getMessage()));
    }


}
