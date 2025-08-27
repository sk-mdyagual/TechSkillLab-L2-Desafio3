package co.com.techskill.lab2.library.service.dummy;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.actor.InspectActor;
import co.com.techskill.lab2.library.actor.LendActor;
import co.com.techskill.lab2.library.actor.ReturnActor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Service
public class OrchestratorService {
    private final PetitionService petitionRepository;
    private final BookService bookService;
    private final List<Actor> actors;

    PetitionService petitionService = new PetitionService();

    public OrchestratorService(PetitionService petitionRepository, List<Actor> actors, BookService bookService) {
        this.petitionRepository = petitionRepository;
        this.bookService = bookService;

        this.actors = List.of(new Actor[]{
                new LendActor(bookService),
                new ReturnActor(bookService),
                new InspectActor(bookService)
        });

    }

    public Flux<String> dummyOrchestrate() {
        return petitionRepository.dummyHighPriority()
                .limitRate(20)
                .publishOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> System.out.println("Inicio orquestación..."))
                .doOnNext(petition ->
                        System.out.println(String.format("Petición encontrada con ID: %s de tipo %s",
                                petition.getPetitionId(),petition.getType())))
                .sort((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()))
                .groupBy(petition -> petition.getType()) //LEND / RETURN
                .flatMap(g -> {
                    String type = g.key();
                    Actor actor = actors.stream()
                            .filter(actor1 -> actor1.supports(type))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("No actor type " + type));
                    System.out.println("Agrupación por tipo");

                    if("LEND".equals(type)){
                        return g.doOnNext(petition -> System.out.println(String.format("Petición [LEND] con ID: %s en cola",
                                        petition.getPetitionId())))
                                .concatMap( petition -> actor.handle(petition)
                                        .doOnSubscribe(s -> System.out.println("Procesando petición con ID "+petition.getPetitionId())))
                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage()))
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    }if("RETURN".equals(type)){
                        return g.doOnNext(petition -> System.out.println(String.format("Petición [RETURN] con ID: %s en cola",
                                        petition.getPetitionId())))
                                .concatMap( petition -> actor.handle(petition)
                                        .doOnSubscribe(s -> System.out.println("Procesando petición con ID "+petition.getPetitionId())))
                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage()))
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    }else{
                        return g.flatMap(petition -> actor.handle(petition)
                                                .doOnSubscribe(s -> System.out.println("Procesando petición de tipo [INSPECT] con ID "+petition.getPetitionId()))
                                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage())),
                                        4)
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    }

                }).timeout(Duration.ofSeconds(5), Flux.just("Timeout exceeded"))
                .doOnNext(s -> System.out.println("Next: "+s))
                .onErrorResume(err-> Flux.just("Error - "+ err.getMessage()))
                .doOnComplete(() -> System.out.println("Orchestration complete"));
    }

}