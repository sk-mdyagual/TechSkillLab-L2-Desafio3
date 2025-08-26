package co.com.techskill.lab2.library.service.impl;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.actor.InspectActor;
import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.repository.IPetitionRepository;
import co.com.techskill.lab2.library.service.IOrchestratorService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile({"default","dummy"})
public class OrchestratorServiceImpl implements IOrchestratorService {

    private final IPetitionRepository petitionRepository;
    private final Map<String, Actor> actorByType;

    public OrchestratorServiceImpl(
            ObjectProvider<IPetitionRepository> petitionRepositoryProvider,
            List<Actor> actors,
            InspectActor inspectActor
    ) {
        this.petitionRepository = petitionRepositoryProvider.getIfAvailable();

        Map<String, Actor> map = new ConcurrentHashMap<>();
        if (actors != null) {
            for (Actor a : actors) {
                for (String t : List.of("INSPECT","RETURN","LEND")) {
                    if (a.supports(t)) map.put(t.toUpperCase(Locale.ROOT), a);
                }
            }
        }
        map.put("INSPECT", inspectActor);
        this.actorByType = Collections.unmodifiableMap(map);
    }

    @Override
    public Flux<String> orchestrate() {

        Flux<Petition> source = (petitionRepository != null)
                ? petitionRepository.findAll()
                : Flux.fromIterable(demoPetitions());
        Flux<String> inspectFlow = source
                .filter(p -> "INSPECT".equalsIgnoreCase(p.getType()))
                .filter(p -> p.getPriority() >= 7)
                .sort(Comparator.comparingInt(Petition::getPriority).reversed())
                .transform(flux -> {
                    Actor actor = actorByType.get("INSPECT");
                    if (actor == null) return Flux.<String>empty();
                    return flux.flatMapSequential(actor::handle, 4, 8);
                });

        Flux<String> others = source
                .filter(p -> !"INSPECT".equalsIgnoreCase(p.getType()))
                .groupBy(p -> p.getType().toUpperCase(Locale.ROOT))
                .flatMap(group -> {
                    String type = group.key();
                    Actor actor = actorByType.get(type);
                    if (actor == null) {
                        return group.map(p -> String.format("[SKIP %s] petition=%s | priority=%d",
                                type, p.getPetitionId(), p.getPriority()));
                    }
                    return group.flatMap(actor::handle, 4);
                });

        return Flux.merge(inspectFlow, others)
                .concatMap(s -> Flux.just(s).delayElements(Duration.ofMillis(200)))
                .doOnSubscribe(s -> System.out.println(">>> ORCHESTRATION START"))
                .doOnNext(msg -> System.out.println("ORCH -> " + msg))
                .doOnComplete(() -> System.out.println(">>> ORCHESTRATION DONE"))
                .onErrorResume(ex -> Flux.just("[ORCH ERROR] " + ex.getMessage()));
    }

    private List<Petition> demoPetitions() {
        return Arrays.asList(
                new Petition("p-1","INSPECT",9,"b-1", LocalDate.now().minusDays(1)),
                new Petition("p-2","LEND",5,"b-2", LocalDate.now()),
                new Petition("p-3","INSPECT",7,"b-3", LocalDate.now()),
                new Petition("p-4","RETURN",4,"b-4", LocalDate.now()),
                new Petition("p-5","INSPECT",10,"b-5", LocalDate.now().minusDays(2))
        );
    }
}