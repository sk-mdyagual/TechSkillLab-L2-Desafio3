package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.repository.IPetitionRepository;
import co.com.techskill.lab2.library.service.IOrchestratorService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrchestratorServiceImpl implements IOrchestratorService {
    private final List<PetitionDTO> petitions = new ArrayList<>();
    private final List<Actor> actors;

    public OrchestratorServiceImpl(List<Actor> actors) {
        this.actors = actors;
        petitions.add(new PetitionDTO("09c09cc8-b", "LEND", 5, "6600ab76-3", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("2f5fca21-b", "INSPECT", 7, "12a13228-0", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("09ef7d35-d", "INSPECT", 4, "297c17d8-4", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("0e6a31b1-f", "RETURN", 4, "6600ab76-3", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("905dfc53-7", "LEND", 5, "6600ab76-3", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("4ebc9aa6-f", "RETURN", 7, "3c24c2fa-3", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("6d7e3b2c-5", "LEND", 4, "eb25c2d4-7", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("2a6214f1-c", "RETURN", 3, "eb25c2d4-7", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("8595a9b7-7", "INSPECT", 7, "51ed516f-a", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("890fd155-0", "LEND", 2, "51ed516f-a", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("2da99667-d", "INSPECT", 4, "1940136a-2", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("742330cf-0", "INSPECT", 6, "12a13228-0", LocalDate.parse("2025-08-22")));
    }
    public Flux<PetitionDTO> dummyFindAll(){
        return Flux.fromIterable(petitions);
    }

    @Override
    public Flux<String> orchestrate() {
        return dummyFindAll()
                .limitRate(20)
                .publishOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> System.out.println("Inicio orquestación..."))
                .doOnNext(petition ->
                        System.out.println(String.format("Petición encontrada con ID: %s de tipo %s",
                                petition.getPetitionId(), petition.getType())))
                .groupBy(petition -> petition.getType())
                .flatMap(g -> {
                    String type = g.key();
                    Actor actor = actors.stream()
                            .filter(a -> a.supports(type))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("No actor type " + type));
                    System.out.println("Agrupación por tipo: " + type);

                    if ("LEND".equals(type)) {
                        // Lógica para LEND (igual que antes)
                        return g.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                .doOnNext(petition -> System.out.println(String.format("Petición [LEND] con ID: %s en cola", petition.getPetitionId())))
                                .concatMap(petition -> actor.handle(petition)
                                        .doOnSubscribe(s -> System.out.println("Procesando petición con ID " + petition.getPetitionId())))
                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                .doOnError(err -> System.out.println("Procesamiento falló - " + err.getMessage()))
                                .onErrorResume(err -> {
                                    System.out.println("Error en LEND: " + err.getMessage());
                                    return Mono.empty();
                                });
                    } else if ("INSPECT".equals(type)) {
                        // Filtrar priority >= 7, ordenar y rutear con flatMapSequential
                        return g.filter(p -> p.getPriority() >= 7)
                                .sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                .doOnNext(petition -> System.out.println(String.format("Petición [INSPECT] con ID: %s en cola", petition.getPetitionId())))
                                .flatMapSequential(petition -> actor.handle(petition)
                                                .doOnSubscribe(s -> System.out.println("Procesando petición de tipo [INSPECT] con ID " + petition.getPetitionId()))
                                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                                .doOnError(err -> System.out.println("Procesamiento falló - " + err.getMessage())),
                                        4)
                                .onErrorResume(err -> {
                                    System.out.println("Error en INSPECT: " + err.getMessage());
                                    return Mono.empty();
                                });
                    } else {
                        // Lógica para RETURN (igual que antes)
                        return g.flatMap(petition -> actor.handle(petition)
                                                .doOnSubscribe(s -> System.out.println("Procesando petición de tipo [RETURN] con ID " + petition.getPetitionId()))
                                                .doOnNext(res -> System.out.println("Proceso exitoso"))
                                                .doOnError(err -> System.out.println("Procesamiento falló - " + err.getMessage())),
                                        4)
                                .onErrorResume(err -> {
                                    System.out.println("Error en RETURN: " + err.getMessage());
                                    return Mono.empty();
                                });
                    }
                })
                .timeout(Duration.ofSeconds(5), Flux.just("Timeout exceeded"))
                .doOnNext(s -> System.out.println("Next: " + s))
                .onErrorResume(err -> {
                    System.out.println("Error general: " + err.getMessage());
                    return Flux.just("Error - " + err.getMessage());
                })
                .doOnComplete(() -> System.out.println("Orchestration complete"));
    }
}
