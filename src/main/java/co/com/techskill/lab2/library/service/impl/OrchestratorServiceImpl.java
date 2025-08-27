package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.actor.InspectActor;
import co.com.techskill.lab2.library.service.IOrchestratorService;
import co.com.techskill.lab2.library.service.dummy.PetitionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Service
public class OrchestratorServiceImpl implements IOrchestratorService {
    private final PetitionService petitionService;
    private final List<Actor> actors;
    private final InspectActor inspectActor;

    public OrchestratorServiceImpl(PetitionService petitionRepository, List<Actor> actors, InspectActor inspectActor) {
        this.petitionService = petitionRepository;
        this.actors = actors;
        this.inspectActor = inspectActor;
    }

    @Override
    public Flux<String> orchestrate() {
        return petitionService.dummyFindAll().sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()))
                .limitRate(20)
                .publishOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> System.out.println("Inicio orquestación..."))
                .doOnNext(petition ->
                        System.out.println(String.format("Petición encontrada con ID: %s de tipo %s",
                                petition.getPetitionId(), petition.getType())))
                .flatMapSequential(petitionDTO -> {
                   if (inspectActor.byPriority(petitionDTO.getPriority())){
                       System.out.println(String.format("Petición enviada a el actor INSPECT con ID: %s ",petitionDTO.getPetitionId()));
                       inspectActor.handle(petitionDTO);
                       return Mono.just(petitionDTO);
                   }
                    return Mono.error(new RuntimeException("Petición con ID "+petitionDTO.getPetitionId()+ " no tiene prioridad mayor ó igual a 7"));
                })
                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()))
                //Fan-out
                .groupBy(petition -> petition.getType()) //LEND / RETURN
                //Fan-in
                .flatMap(g -> {
                    String type = g.key();
                    Actor actor = actors.stream()
                            .filter(actor1 -> actor1.supports(type))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("No actor type " + type));
                    System.out.println("Agrupación por tipo");
                    if ("LEND".equals(type)) {
                        return g.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                .doOnNext(petition -> System.out.println(String.format("Petición [LEND] con ID: %s en cola",
                                        petition.getPetitionId())))
                                .concatMap(petition -> actor.handle(petition)
                                        .doOnSubscribe(s -> System.out.println("Procesando petición con ID " + petition.getPetitionId())))
                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err -> System.out.println("Procesamiento falló - " + err.getMessage()))
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    } else {
                        return g.flatMap(petition -> actor.handle(petition)
                                                .doOnSubscribe(s -> System.out.println("Procesando petición de tipo [RETURN] con ID " + petition.getPetitionId()))
                                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                                .doOnError(err -> System.out.println("Procesamiento falló - " + err.getMessage())),
                                        4)
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    }

                }).timeout(Duration.ofSeconds(5), Flux.just("Timeout exceeded")) //Control
                .doOnNext(s -> System.out.println("Next: " + s))
                .onErrorResume(err -> Flux.just("Error - " + err.getMessage()))
                .doOnComplete(() -> System.out.println("Orchestration complete"));
    }
}
