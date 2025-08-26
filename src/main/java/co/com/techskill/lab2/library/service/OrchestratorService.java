package co.com.techskill.lab2.library.service;

import co.com.techskill.lab2.library.actor.InspectActor;
import co.com.techskill.lab2.library.domain.entity.Petition;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

/**
 * Orquestador para el desafío #3:
 * - INSPECT: priority >= 7, orden DESC y ruteo con flatMapSequential
 * - onErrorResume está dentro del actor
 * - Logs del orquestador y ritmo de salida amigable
 */
@Service
@Profile({"default","dummy"}) // orquestador dummy (sin Mongo)
public class OrchestratorService {

    private final InspectActor inspectActor;

    public OrchestratorService(InspectActor inspectActor) {
        this.inspectActor = inspectActor;
    }

    /** Punto de entrada principal (si ya existe este método, reemplaza SOLO su cuerpo). */
    public Flux<String> orchestrate(Flux<Petition> petitions) {

        // --- INSPECT: priority >= 7, orden DESC, ruteo con flatMapSequential ---
        Flux<String> inspectFlow = petitions
                .filter(p -> inspectActor.supports(p.getType()))
                .filter(p -> p.getPriority() >= 7)
                .sort(Comparator.comparingInt(Petition::getPriority).reversed())
                .flatMapSequential(inspectActor::handle, /*concurrency*/4, /*prefetch*/8);

        // --- Otros tipos (si no tienes más actores, emite solo un mensaje informativo) ---
        Flux<String> others = petitions
                .filter(p -> !"INSPECT".equalsIgnoreCase(p.getType()))
                .map(p -> String.format("[OTHER %s] petition=%s | priority=%d",
                        p.getType(), p.getPetitionId(), p.getPriority()));

        // Merge + logs + ritmo de salida
        return Flux.merge(inspectFlow, others)
                .concatMap(s -> Flux.just(s).delayElements(Duration.ofMillis(200))) // pacing amigable
                .doOnSubscribe(s -> System.out.println(">>> ORCHESTRATOR START"))
                .doOnNext(msg -> System.out.println("ORCH -> " + msg))
                .doOnComplete(() -> System.out.println(">>> ORCHESTRATOR DONE"));
    }

    /** Overload por si llamas con lista desde el controlador. */
    public Flux<String> orchestrate(List<Petition> list) {
        return orchestrate(Flux.fromIterable(list));
    }
}
