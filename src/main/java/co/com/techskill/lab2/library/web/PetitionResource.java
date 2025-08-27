package co.com.techskill.lab2.library.web;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.service.IPetitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/petitions")
public class PetitionResource {
    private final IPetitionService petitionService;

    public PetitionResource(IPetitionService petitionService){
        this.petitionService = petitionService;
    }

    @GetMapping("/all")
    public Flux<PetitionDTO> getAllPetitions(){
        return petitionService.findALl();
    }

    @PostMapping("/id")
    public Mono<ResponseEntity<PetitionDTO>> findByPetitionId(@RequestBody PetitionDTO petitionDTO){
        return petitionService.findById(petitionDTO.getPetitionId())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/id/intermitence")
    public Mono<ResponseEntity<String>> findByPetitionIdItermitence(@RequestBody PetitionDTO petitionDTO){
        return petitionService.simulateIntermittency(petitionDTO)
                .map(ResponseEntity::ok);

    }

    @PostMapping("/save")
    public Mono<ResponseEntity<PetitionDTO>> savePetition(@RequestBody PetitionDTO petitionDTO){
        return petitionService.save(petitionDTO)
                .map(ResponseEntity::ok);

    }


    @PostMapping("/revisar")
    public Flux<String> checkPetitions(@RequestBody PetitionDTO petitionDTO) {
        return petitionService.checkPriorities(petitionDTO);
    }

    @PostMapping("/inspect/create-sample")
    public Flux<ResponseEntity<PetitionDTO>> createSampleInspectPetitions() {
        PetitionDTO highPriorityInspect = new PetitionDTO("INSPECT", 9, "BOOK001");
        PetitionDTO mediumPriorityInspect = new PetitionDTO("INSPECT", 7, "BOOK002");
        PetitionDTO lowPriorityInspect = new PetitionDTO("INSPECT", 5, "BOOK003");
        PetitionDTO veryHighPriorityInspect = new PetitionDTO("INSPECT", 10, "BOOK004");

        return Flux.just(highPriorityInspect, mediumPriorityInspect, lowPriorityInspect, veryHighPriorityInspect)
                .flatMap(petitionService::save)
                .map(ResponseEntity::ok);
    }
}
