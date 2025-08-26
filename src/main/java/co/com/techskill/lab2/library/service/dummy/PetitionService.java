package co.com.techskill.lab2.library.service.dummy;

import co.com.techskill.lab2.library.domain.entity.Petition;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PetitionService {
    private final List<Petition> petitions = new ArrayList<>();

    public PetitionService(){
        petitions.add(new Petition("09c09cc8-b", "LEND", 5, "6600ab76-3", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("2f5fca21-b", "RETURN", 7, "12a13228-0", LocalDate.parse("2025-08-20")));
        petitions.add(new Petition("4c9ef769-9", "INSPECT", 7, "51ed516f-a", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("5b2dae36-f", "LEND", 3, "51ed516f-a", LocalDate.parse("2025-08-15")));
        petitions.add(new Petition("ad4801f0-9", "RETURN", 5, "51ed516f-a", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("9cc825c1-7", "INSPECT", 7, "12a13228-0", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("d5120259-4", "LEND", 4, "11b553eb-b", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("09ef7d35-d", "RETURN", 4, "297c17d8-4", LocalDate.parse("2025-08-20")));
        petitions.add(new Petition("0e6a31b1-f", "INSPECT", 4, "6600ab76-3", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("905dfc53-7", "LEND", 5, "6600ab76-3", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("4ebc9aa6-f", "RETURN", 7, "3c24c2fa-3", LocalDate.parse("2025-08-20")));
        petitions.add(new Petition("6d7e3b2c-5", "LEND", 7, "eb25c2d4-7", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("2a6214f1-c", "RETURN", 3, "eb25c2d4-7", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("8595a9b7-7", "INSPECT", 7, "51ed516f-a", LocalDate.parse("2025-08-20")));
        petitions.add(new Petition("890fd155-0", "LEND", 2, "51ed516f-a", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("2da99667-d", "INSPECT", 4, "1940136a-2", LocalDate.parse("2025-08-19")));
        petitions.add(new Petition("cbfdd0aa-c", "RETURN", 7, "1940136a-2", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("0ff09c9e-5", "LEND", 6, "11b553eb-b", LocalDate.parse("2025-08-25")));
        petitions.add(new Petition("86084e60-e", "RETURN", 7, "11b553eb-b", LocalDate.parse("2025-08-15")));
        petitions.add(new Petition("742330cf-0", "LEND", 7, "12a13228-0", LocalDate.parse("2025-08-12")));
    }

    public Flux<Petition> dummyFindAll(){
        return Flux.fromIterable(petitions);
    }

    public Mono<Petition> dummyFindById(String id){
        return Mono.justOrEmpty(
                petitions.stream()
                        .filter(petition -> petition.getPetitionId().equals(id))
                        .findFirst()
        );
    }

}
