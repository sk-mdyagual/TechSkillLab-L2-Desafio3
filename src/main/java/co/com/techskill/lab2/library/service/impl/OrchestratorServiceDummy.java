package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.actor.DummyActor;
import co.com.techskill.lab2.library.service.IOrchestratorService;
import co.com.techskill.lab2.library.service.dummy.PetitionService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Primary
public class OrchestratorServiceDummy implements IOrchestratorService {
    private final PetitionService petitionService;
    private final DummyActor dummyActor;

    public OrchestratorServiceDummy(PetitionService petitionService, DummyActor dummyActor) {
        this.petitionService = petitionService;
        this.dummyActor = dummyActor;
    }

    @Override
    public Flux<String> orchestrate() {
        return petitionService.dummyFindAll()
                .limitRate(20)
                .groupBy(petitionDTO -> petitionDTO.getPriority()>=7)
                .flatMap(group -> {
                    if(group.key()){
                        return group.sort((a,b) -> Integer.compare(b.getPriority(), a.getPriority()))
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
                        /*return group.flatMapSequential(petition -> dummyActor.handle(petition)
                                //.doOnSubscribe(s -> System.out.println("Procesando petición con ID "+petition.getPetitionId()))
                                //.doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err-> System.out.println("Procesamiento falló - "+err.getMessage())),
                                4)
                                .onErrorContinue((err, p) -> System.out.println("Petitición omitida " + err.getMessage()));*/
                    }
                });
    }
}
