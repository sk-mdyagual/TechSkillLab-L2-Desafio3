package co.com.techskill.lab2.library.service.dummy;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PetitionService {
    private final List<PetitionDTO> petitions = new ArrayList<>();

    public PetitionService(){
        petitions.add(new PetitionDTO("a3f7e8d9-c", "LEND", 3, "297c17d8-4", LocalDate.parse("2025-08-28")));
        petitions.add(new PetitionDTO("b8c4f2a1-e", "RETURN", 6, "3c24c2fa-3", LocalDate.parse("2025-08-29")));
        petitions.add(new PetitionDTO("c9d5e3b7-f", "INSPECT", 4, "eb25c2d4-7", LocalDate.parse("2025-08-30")));
        petitions.add(new PetitionDTO("d2a6f8c4-a", "LEND", 8, "1940136a-2", LocalDate.parse("2025-08-31")));
        petitions.add(new PetitionDTO("e7b3c9d5-b", "RETURN", 2, "11b553eb-b", LocalDate.parse("2025-09-01")));
        petitions.add(new PetitionDTO("f4c8e2a6-d", "LEND", 9, "12a13228-0", LocalDate.parse("2025-09-02")));
        petitions.add(new PetitionDTO("a1d7f3b9-e", "INSPECT", 5, "51ed516f-a", LocalDate.parse("2025-09-03")));
        petitions.add(new PetitionDTO("b6e4c8a2-f", "RETURN", 7, "6600ab76-3", LocalDate.parse("2025-09-04")));
        petitions.add(new PetitionDTO("c3f9d5b1-a", "LEND", 1, "297c17d8-4", LocalDate.parse("2025-09-05")));
        petitions.add(new PetitionDTO("d8a4e7c3-b", "INSPECT", 6, "3c24c2fa-3", LocalDate.parse("2025-09-06")));
        petitions.add(new PetitionDTO("e5b2f8d4-c", "RETURN", 4, "eb25c2d4-7", LocalDate.parse("2025-09-07")));
        petitions.add(new PetitionDTO("f1c6a3e9-d", "LEND", 3, "1940136a-2", LocalDate.parse("2025-09-08")));
        petitions.add(new PetitionDTO("a9e7b4c2-e", "INSPECT", 8, "11b553eb-b", LocalDate.parse("2025-09-09")));
        petitions.add(new PetitionDTO("b4d8f1a5-f", "RETURN", 5, "12a13228-0", LocalDate.parse("2025-09-10")));
        petitions.add(new PetitionDTO("c7a3e6d2-a", "LEND", 7, "51ed516f-a", LocalDate.parse("2025-09-11")));
    }

    public Flux<PetitionDTO> findAll(){
        return Flux.fromIterable(petitions);
    }

    public Mono<PetitionDTO> findById(String id){
        return Mono.justOrEmpty(
                petitions.stream()
                        .filter(petitionDTO -> petitionDTO.getPetitionId().equals(id))
                        .findFirst()
        );
    }
}
