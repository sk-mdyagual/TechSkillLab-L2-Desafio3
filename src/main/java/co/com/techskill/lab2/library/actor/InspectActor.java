package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import reactor.core.publisher.Mono;

public class InspectActor implements Actor {

    @Override
    public boolean supports(String type) {
        return "INSPECT".equals(type);
    }

    @Override
    public Mono<String> handle(PetitionDTO petition) {
        if (petition.getPriority() >= 7) {
            System.out.println("Procesando petición [INSPECT] con ID " + petition.getPetitionId() + " y prioridad " + petition.getPriority());
            return Mono.just("Petición [INSPECT] procesada: " + petition.getPetitionId());
        } else {
            System.out.println("Petición [INSPECT] con ID " + petition.getPetitionId() + " omitida por prioridad insuficiente");
            return Mono.just("Petición [INSPECT] omitida: " + petition.getPetitionId());
        }
    }
}
