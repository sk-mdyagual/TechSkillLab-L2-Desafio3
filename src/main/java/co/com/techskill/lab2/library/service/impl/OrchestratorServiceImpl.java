package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.actor.PetitionService;

import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.service.IOrchestratorService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Service
public class OrchestratorServiceImpl implements IOrchestratorService {
    private final PetitionService petitionRepository;
    private final List<Actor> actors;

    public OrchestratorServiceImpl(PetitionService petitionRepository, List<Actor> actors) {
        this.petitionRepository = petitionRepository;
        this.actors = actors;
    }

    @Override
    public Flux<String> orchestrate() {
        return petitionRepository.dummyFindAll()
                .publish(shared -> {
                    // Flujo paralelo para inspecciones de alta prioridad
                    Flux<String> inspectionFlow = shared
                            .filter(p -> p.getPriority() > 6)
                            .sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()))
                            .concatMap(petition -> {
                                Actor actor = actors.stream()
                                        .filter(a -> a.supports("INSPECT"))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("No actor for INSPECT"));
                                return actor.handle(petition)
                                        .doOnSubscribe(s -> System.out.println("Procesando petición [INSPECT] con ID " + petition.getPetitionId()))
                                        .doOnNext(res -> System.out.println("Proceso [INSPECT] exitoso"))
                                        .doOnError(err -> System.out.println("Procesamiento [INSPECT] falló - " + err.getMessage()))
                                        .onErrorContinue((err, p) -> System.out.println("Petición [INSPECT] omitida " + err.getMessage()));
                            });

                    // Flujo original para LEND/RETURN con peticiones restantes
                    Flux<String> originalFlow = shared
                            .filter(p -> p.getPriority() <= 6)
                            .limitRate(29)
                            .publishOn(Schedulers.boundedElastic())
                            .doOnSubscribe(s -> System.out.println("Inicio orquestación LEND/RETURN..."))
                            .doOnNext(petition ->
                                    System.out.println(String.format("Petición encontrada con ID: %s de tipo %s",
                                            petition.getPetitionId(), petition.getType())))
                            .groupBy(petition -> petition.getType())
                            .flatMap(g -> {
                                String type = g.key();
                                Actor actor = actors.stream()
                                        .filter(actor1 -> actor1.supports(type))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("No actor type " + type));
                                System.out.println("Agrupación por tipo: " + type);

                                if ("LEND".equals(type)) {
                                    return g.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                            .doOnNext(petition -> System.out.println(String.format("Petición [LEND] con ID: %s en cola",
                                                    petition.getPetitionId())))
                                            .concatMap(petition -> actor.handle(petition)
                                                    .doOnSubscribe(s -> System.out.println("Procesando petición con ID " + petition.getPetitionId())))
                                            .doOnNext(res -> System.out.println("Proceso exitoso"))
                                            .doOnError(err -> System.out.println("Procesamiento falló - " + err.getMessage()))
                                            .onErrorContinue((err, p) -> System.out.println("Petición omitida " + err.getMessage()));
                                } else { // Asumimos RETURN y otros tipos futuros
                                    return g.flatMap(petition -> actor.handle(petition)
                                                    .doOnSubscribe(s -> System.out.println("Procesando petición de tipo [" + type + "] con ID " + petition.getPetitionId()))
                                                    .doOnNext(res -> System.out.println("Proceso exitoso"))
                                                    .doOnError(err -> System.out.println("Procesamiento falló - " + err.getMessage())),
                                            4) // Concurrencia para RETURN
                                            .retry(3)
                                            .onErrorContinue((err, p) -> System.out.println("Petición omitida " + err.getMessage()));
                                }
                            });

                    return Flux.merge(inspectionFlow, originalFlow);
                })
                .timeout(Duration.ofSeconds(60), Flux.just("Timeout exceeded"))
                .doOnNext(s -> System.out.println("Next: " + s))
                .onErrorResume(err -> Flux.just("Error - " + err.getMessage()))
                .doOnComplete(() -> System.out.println("Orchestration complete"));
    }
}
