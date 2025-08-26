package co.com.techskill.lab2.library.service.dummy;

import co.com.techskill.lab2.library.actor.Actor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Service
public class OrchestratorService {

    private final PetitionService petitionService;
    private final List<Actor> actors;

    public OrchestratorService(PetitionService petitionService, List<Actor> actors) {
        this.petitionService = petitionService;
        this.actors = actors;
    }

    public Flux<String> orchestrate() {
        return petitionService.dummyFindAll()
                .filter(petition -> petition.getPriority() >= 7) //Prioridad mayor o igual a 7
                .sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority())) //Prioridad descendente
                .limitRate(20)
                .publishOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> System.out.println("Inicio orquestación..."))
                .doOnNext(petition ->
                        System.out.println(String.format("Petición encontrada con ID: %s de tipo %s",
                                petition.getPetitionId(),petition.getType())))
                //Fan-out
                .groupBy(petition -> petition.getType()) //LEND / RETURN / INSPECT
                //Fan-in
                .flatMap(g -> {
                    String type = g.key();
                    Actor actor = actors.stream()
                            .filter(actor1 -> actor1.supports(type))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("No actor type " + type));
                    System.out.println("Agrupación por tipo");

                    if("LEND".equals(type)){
                        return g.sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                .doOnNext(petition -> System.out.println(String.format("Petición [LEND] con ID: %s en cola",
                                        petition.getPetitionId())))
                                .concatMap( petition -> actor.handle(petition)
                                        .doOnSubscribe(s -> System.out.println("Procesando petición con ID "+petition.getPetitionId())))
                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage()))
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    }else if("RETURN".equals(type)){
                        return g.flatMap(petition -> actor.handle(petition)
                                                .doOnSubscribe(s -> System.out.println("Procesando petición de tipo [RETURN] con ID "+petition.getPetitionId()))
                                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage())),
                                        4)
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    } else {
                        return g.flatMap(petition -> actor.handle(petition)
                                                .doOnSubscribe(s -> System.out.println("Procesando petición de tipo [INSPECT] con ID "+petition.getPetitionId()))
                                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage())),
                                        4)
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    }

                }).timeout(Duration.ofSeconds(5), Flux.just("Timeout exceeded")) //Control
                .doOnNext(s -> System.out.println("Next: "+s))
                .onErrorResume(err-> Flux.just("Error - "+ err.getMessage()))
                .doOnComplete(() -> System.out.println("Orchestration complete"));
    }
}
