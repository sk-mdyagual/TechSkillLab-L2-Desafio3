package co.com.techskill.lab2.library.Actores;

import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.repository.IPetitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
@Component
public class RetornarActores implements Actores {

    private  final IPetitionRepository petitionRepository ;

    public RetornarActores(IPetitionRepository petitionRepository) {
        this.petitionRepository = petitionRepository;
    }

    @Override
    public boolean supports(String type) {
        return "RETURN".equals(type);
    }

    @Override
    public Mono<String> handle(Petition petition) {
        return Mono.zip(
                        Mono.just(petition),
                        petitionRepository.findByPetitionId(petition.getPetitionId()))
                .delayElement(Duration.ofMillis(50))
                .map(t ->
                        String.format("[RETURN] petition : %s priority %d",t.getT2().getPetitionId(), t.getT2().getPriority())
                );
    }

}
