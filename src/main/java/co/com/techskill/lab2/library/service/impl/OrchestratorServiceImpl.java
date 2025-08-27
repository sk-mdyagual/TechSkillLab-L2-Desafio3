package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.config.PetitionMapper;
import co.com.techskill.lab2.library.repository.IPetitionRepository;
import co.com.techskill.lab2.library.service.IOrchestratorService;
import co.com.techskill.lab2.library.service.dummy.PetitionServiceDummy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Service
public class OrchestratorServiceImpl implements IOrchestratorService {
    private final IPetitionRepository petitionRepository;
    private final PetitionServiceDummy petitionServiceDummy;
    private final PetitionMapper petitionMapper;
    private final List<Actor> actors;

    public OrchestratorServiceImpl(IPetitionRepository petitionRepository, List<Actor> actors,PetitionServiceDummy petitionServiceDummy,PetitionMapper petitionMapper ) {
        this.petitionRepository = petitionRepository;
        this.actors = actors;
        this.petitionMapper = petitionMapper;
        this.petitionServiceDummy = petitionServiceDummy;
    }

    @Override
    public Flux<String> orchestrate() {
        return petitionServiceDummy.filterPriorityInspectPetitions()
                .limitRate(20)
                .publishOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> System.out.println("Inicio orquestación..."))
                .doOnNext(petition ->
                        System.out.println(String.format("Petición encontrada con ID: %s de tipo %s",
                                petition.getPetitionId(),petition.getType())))
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

                        return g.sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                .doOnNext(petition -> System.out.println(String.format("Petición con ID: %s en cola",
                                        petition.getPetitionId()))).doOnNext(
                                        petition -> System.out.println(String.format("Petición con Priority: %s en cola",
                                                petition.getPriority()))
                                )
                                .concatMap( petition -> actor.handle(petitionMapper.toEntity(petition))
                                        .doOnSubscribe(s -> System.out.println("Procesando petición con ID "+petition.getPetitionId())))
                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage()))
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));


                }).timeout(Duration.ofSeconds(200), Flux.just("Timeout exceeded")) //Control
                .doOnNext(s -> System.out.println("Next: "+s))
                .onErrorResume(err-> Flux.just("Error - "+ err.getMessage()))
                .doOnComplete(() -> System.out.println("Orchestration complete"));
    }
}
