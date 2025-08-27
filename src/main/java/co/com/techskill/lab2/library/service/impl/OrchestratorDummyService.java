package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.actor.DummyActor;
import co.com.techskill.lab2.library.service.dummyServices.PetitionDummyServices;
import co.com.techskill.lab2.library.service.IOrchestratorService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Primary
public class OrchestratorDummyService implements IOrchestratorService {
    private final PetitionDummyServices PetitionDummyServices;
    private final DummyActor dummyActor;

    public OrchestratorDummyService(PetitionDummyServices PetitionDummyServices, DummyActor dummyActor) {
        this.PetitionDummyServices = PetitionDummyServices;
        this.dummyActor = dummyActor;
    }

    @Override
    public Flux<String> orchestrate() {
        return PetitionDummyServices.dummyFindAll()
                .limitRate(20)
                .doOnSubscribe(s -> System.out.println("Inicio orquestación..."))
                .doOnNext(petition ->
                        System.out.println(String.format("Petición encontrada con ID: %s de tipo %s",
                                petition.getPetitionId(),petition.getType())))
                .groupBy(petitionDTO -> petitionDTO.getPriority()>=7)
                .flatMap(g -> {
                    if(g.key()){
                        return g.sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                .doOnNext(petition -> System.out.println(String.format("Petición con ID: %s en cola",
                                        petition.getPetitionId())))
                                .flatMapSequential( petition -> dummyActor.handle(petition)
                                        .doOnSubscribe(s -> System.out.println("Procesando petición con ID "+petition.getPetitionId())))
                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage()))
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));
                    }
                    else{
                        return Mono.empty();
                    }
                }).timeout(Duration.ofSeconds(5), Flux.just("Timeout exceeded")) //Control
                .doOnNext(s -> System.out.println("Next: "+s))
                .onErrorResume(err-> Flux.just("Error - "+ err.getMessage()))
                .doOnComplete(() -> System.out.println("Orchestration complete"));
    }

}
