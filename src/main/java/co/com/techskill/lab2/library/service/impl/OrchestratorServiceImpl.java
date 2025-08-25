package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.domain.entity.Petition;     
import co.com.techskill.lab2.library.repository.IPetitionRepository;
import co.com.techskill.lab2.library.service.IOrchestratorService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDate;                                    
import java.util.List;

@Service
public class OrchestratorServiceImpl implements IOrchestratorService {
    private final IPetitionRepository petitionRepository;
    private final List<Actor> actors;

    public OrchestratorServiceImpl(IPetitionRepository petitionRepository, List<Actor> actors) {
        this.petitionRepository = petitionRepository;
        this.actors = actors;
    }

  @Override
public Flux<String> orchestrate() {
    // 1) Fuente con timeout temprano y fallback
    Flux<Petition> source = petitionRepository.findAll()
        .timeout(Duration.ofSeconds(2))                    // ⬅️ corta la espera de conexión
        .onErrorResume(e -> {
            System.out.println("[Fallback] BD no accesible: " + e.getClass().getSimpleName()
                    + " - " + e.getMessage());
            return seedInMemory();                         // ⬅️ usa datos en memoria
        })
        .switchIfEmpty(Flux.defer(this::seedInMemory)      // ⬅️ si la BD está vacía
            .doFirst(() -> System.out.println("[Fallback] BD vacía. Usando seed en memoria.")));

    // 2) El resto del pipeline igual
    return source
        .limitRate(20)
        .publishOn(Schedulers.boundedElastic())
        .doOnSubscribe(s -> System.out.println("Inicio orquestación..."))
        .doOnNext(p -> System.out.printf("Petición encontrada con ID: %s de tipo %s%n",
                p.getPetitionId(), p.getType()))
        .groupBy(Petition::getType)
        .flatMap(g -> {
            String type = g.key();
            Actor actor = actors.stream()
                .filter(a -> a.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No actor type " + type));

            System.out.println("Agrupación por tipo: " + type);

            if ("LEND".equals(type)) {
                return g.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                        .doOnNext(p -> System.out.printf("Petición [LEND] ID=%s en cola%n", p.getPetitionId()))
                        .concatMap(p -> actor.handle(p)
                            .doOnSubscribe(s -> System.out.println("Procesando petición LEND ID " + p.getPetitionId())))
                        .doOnError(err -> System.out.println("Procesamiento falló - " + err.getMessage()))
                        .onErrorContinue((err, p) -> System.out.println("Petición omitida " + err.getMessage()));
            } else if ("RETURN".equals(type)) {
                return g.flatMap(p -> actor.handle(p)
                            .doOnSubscribe(s -> System.out.println("Procesando petición RETURN ID " + p.getPetitionId()))
                            .doOnError(err -> System.out.println("Procesamiento falló - " + err.getMessage())),
                            4)
                        .onErrorContinue((err, p) -> System.out.println("Petición omitida " + err.getMessage()));
            } else if ("INSPECT".equals(type)) {
                return g.filter(p -> p.getPriority() != null && p.getPriority() >= 7)
                        .sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                        .doOnNext(p -> System.out.printf("Petición [INSPECT] ID=%s prio=%d en cola%n",
                                p.getPetitionId(), p.getPriority()))
                        .flatMapSequential(p ->
                                actor.handle(p).onErrorResume(ex -> Flux.just(
                                        "[FALLBACK INSPECT] id=%s cause=%s"
                                            .formatted(p.getPetitionId(), ex.getClass().getSimpleName())
                                )),
                                4, 4)
                        .doOnError(err -> System.out.println("Procesamiento INSPECT falló - " + err.getMessage()))
                        .onErrorContinue((err, p) -> System.out.println("Petición INSPECT omitida " + err.getMessage()));
            } else {
                return g.doOnNext(p -> System.out.printf("Tipo no soportado: %s (id=%s)%n",
                                type, p.getPetitionId()))
                        .flatMap(p -> Flux.empty());
            }
        })
        // Este timeout global ya no es crítico, pero puede quedarse
        .timeout(Duration.ofSeconds(5), Flux.just("Timeout exceeded"))
        .doOnNext(s -> System.out.println("Next: " + s))
        .onErrorResume(err -> Flux.just("Error - " + err.getMessage()))
        .doOnComplete(() -> System.out.println("Orchestration complete"));
}

    /**
     * Semilla en memoria para pruebas sin Mongo.
     */
    private Flux<Petition> seedInMemory() {
        List<Petition> seed = List.of(
            new Petition("i-001", "INSPECT", 10, "B001", LocalDate.now()),
            new Petition("i-002", "INSPECT",  8, "B002", LocalDate.now()),
            new Petition("l-001", "LEND",      6, "B003", LocalDate.now()),
            new Petition("r-001", "RETURN",    7, "B004", LocalDate.now()),
            new Petition("i-003", "INSPECT",   7, "B005", LocalDate.now())
        );
        return Flux.fromIterable(seed);
    }
}
