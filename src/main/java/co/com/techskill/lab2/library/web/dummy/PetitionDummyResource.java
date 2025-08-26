package co.com.techskill.lab2.library.web.dummy;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.service.dummy.PetitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/dummy/petitions")
public class PetitionDummyResource {
    private final PetitionService petitionService;

    public PetitionDummyResource(PetitionService petitionService) {
        this.petitionService = petitionService;
    }

    @GetMapping("/all")
    public Flux<PetitionDTO> getAllPetitions() {
        return petitionService.dummyFindAll();
    }

    @PostMapping("/id")
    public Mono<ResponseEntity<PetitionDTO>> findByPetitionId(@RequestBody PetitionDTO petitionDTO) {
        return petitionService.dummyFindById(petitionDTO.getPetitionId())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/processReturns")
    public Mono<String> processPetition(@RequestBody PetitionDTO petitionDTO) {
        return petitionService.processReturnPetitions(petitionDTO.getPetitionId());

    }

    @GetMapping("/highPriority")
    public Flux<PetitionDTO> dummyHighPriority(){
        return petitionService.dummyHighPriority();
    }
}
