package co.com.techskill.lab2.library.Actores;

import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.repository.IPetitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class LendActores implements Actores {

    private  final IPetitionRepository petitionRepository ;

    public LendActores(IPetitionRepository petitionRepository) {
        this.petitionRepository = petitionRepository;
    }

    @Override
    public boolean supports(String type) {
        return "LEND".equals(type);
    }

    @Override
    public Mono<String> handle(Petition petition) {
        return Mono.zip(
                        Mono.just(petition),
                        petitionRepository.findByPetitionId(petition.getPetitionId()))
                .delayElement(Duration.ofMillis(200))
                .map(t ->
                        String.format("[LEND] petition : %s priority %d",t.getT2().getPetitionId(), t.getT2().getPriority())
                );
    }
}
