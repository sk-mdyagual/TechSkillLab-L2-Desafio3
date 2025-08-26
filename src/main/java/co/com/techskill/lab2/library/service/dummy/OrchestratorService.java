package co.com.techskill.lab2.library.service.dummy;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.service.IOrchestratorService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Service
public class OrchestratorService implements IOrchestratorService {
    private final PetitionService petitionRepository;
    private final List<Actor> actors;

    public OrchestratorService(PetitionService petitionRepository, List<Actor> actors) {
        this.petitionRepository = petitionRepository;
        this.actors = actors;
    }

    @Override
    public Flux<String> orchestrate() {
        return petitionRepository.dummyFindAll()
                .limitRate(20)
                .publishOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> System.out.println("Inicio orquestación..."))
                .doOnNext(petition ->
                        System.out.printf("Petición encontrada con ID: %s de tipo %s%n",
                                petition.getPetitionId(),petition.getType()))
                //Fan-out
                .groupBy(Petition::getType) //LEND / RETURN
                //Fan-in
                .flatMap(g -> {
                    String type = g.key();
                    Actor actor = actors.stream()
                            .filter(actor1 -> actor1.supports(type))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("No actor type " + type));
                    System.out.println("Agrupación por tipo");
                    if ("INSPECT".equals(type)){
                        return g.filter(petition -> petition.getPriority() >= 7)
                                .sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                .doOnNext(petition -> System.out.println(String.format("Petición [INSPECT] con ID: %s en cola",
                                        petition.getPetitionId())))
                                .flatMapSequential( petition -> actor.handle(petition)
                                        .doOnSubscribe(s -> System.out.println("Procesando petición con ID "+petition.getPetitionId())))
                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage()))
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    }
                    else if("LEND".equals(type)){
                        return g.sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                .doOnNext(petition -> System.out.println(String.format("Petición [LEND] con ID: %s en cola",
                                        petition.getPetitionId())))
                                .concatMap( petition -> actor.handle(petition)
                                        .doOnSubscribe(s -> System.out.println("Procesando petición con ID "+petition.getPetitionId())))
                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage()))
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    }else{
                        return g.flatMap(petition -> actor.handle(petition)
                                .doOnSubscribe(s -> System.out.println("Procesando petición de tipo [RETURN] con ID "+petition.getPetitionId()))
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
