package co.com.techskill.lab2.library.service;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPetitionServiceDummy {
    Flux<PetitionDTO> dummyFindAll();
    Mono<PetitionDTO> dummyFindById(String id);
}
