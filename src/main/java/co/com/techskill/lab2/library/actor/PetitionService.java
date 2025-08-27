package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Petition;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class PetitionService {
    private final List<Petition> petitions = new ArrayList<>();
    private final CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("petitionService");

    public PetitionService(){
        petitions.add(new Petition("09c09cc8-b", "LEND", 8, "6600ab76-3", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("2f5fca21-b", "RETURN", 7, "12a13228-0", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("4c9ef769-9", "LEND", 7, "51ed516f-a", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("5b2dae36-f", "LEND", 8, "51ed516f-a", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("ad4801f0-9", "RETURN", 5, "51ed516f-a", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("9cc825c1-7", "RETURN", 7, "12a13228-0", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("d5120259-4", "LEND", 4, "11b553eb-b", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("09ef7d35-d", "RETURN", 4, "297c17d8-4", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("0e6a31b1-f", "RETURN", 4, "6600ab76-3", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("905dfc53-7", "LEND", 5, "6600ab76-3", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("4ebc9aa6-f", "RETURN", 7, "3c24c2fa-3", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("6d7e3b2c-5", "LEND", 9, "eb25c2d4-7", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("2a6214f1-c", "RETURN", 3, "eb25c2d4-7", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("8595a9b7-7", "RETURN", 7, "51ed516f-a", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("890fd155-0", "LEND", 9, "51ed516f-a", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("2da99667-d", "LEND", 9, "1940136a-2", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("cbfdd0aa-c", "RETURN", 7, "1940136a-2", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("0ff09c9e-5", "LEND", 6, "11b553eb-b", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("86084e60-e", "RETURN", 7, "11b553eb-b", LocalDate.parse("2025-07-25")));
        petitions.add(new Petition("742330cf-0", "LEND", 9, "12a13228-0", LocalDate.parse("2025-07-25")));

        // INSPECT Petitions
        petitions.add(new Petition("inspect-01", "INSPECT", 8, "6600ab76-3", LocalDate.parse("2025-08-01")));
        petitions.add(new Petition("inspect-02", "INSPECT", 9, "12a13228-0", LocalDate.parse("2025-08-01")));
        petitions.add(new Petition("inspect-03", "INSPECT", 7, "51ed516f-a", LocalDate.parse("2025-08-01")));
        petitions.add(new Petition("inspect-04", "INSPECT", 10, "11b553eb-b", LocalDate.parse("2025-08-01")));
        petitions.add(new Petition("inspect-05", "INSPECT", 8, "297c17d8-4", LocalDate.parse("2025-08-01")));
        petitions.add(new Petition("inspect-06", "INSPECT", 9, "3c24c2fa-3", LocalDate.parse("2025-08-01")));
        petitions.add(new Petition("inspect-07", "INSPECT", 7, "eb25c2d4-7", LocalDate.parse("2025-08-01")));
        petitions.add(new Petition("inspect-08", "INSPECT", 10, "1940136a-2", LocalDate.parse("2025-08-01")));
        petitions.add(new Petition("inspect-09", "INSPECT", 8, "6600ab76-3", LocalDate.parse("2025-08-02")));
        petitions.add(new Petition("inspect-10", "INSPECT", 9, "12a13228-0", LocalDate.parse("2025-08-02")));
        petitions.add(new Petition("inspect-11", "INSPECT", 5, "51ed516f-a", LocalDate.parse("2025-08-02")));
        petitions.add(new Petition("inspect-12", "INSPECT", 6, "11b553eb-b", LocalDate.parse("2025-08-02")));
        petitions.add(new Petition("inspect-13", "INSPECT", 8, "297c17d8-4", LocalDate.parse("2025-08-03")));
        petitions.add(new Petition("inspect-14", "INSPECT", 9, "3c24c2fa-3", LocalDate.parse("2025-08-03")));
        petitions.add(new Petition("inspect-15", "INSPECT", 7, "eb25c2d4-7", LocalDate.parse("2025-08-03")));
        petitions.add(new Petition("inspect-16", "INSPECT", 10, "1940136a-2", LocalDate.parse("2025-08-03")));
        petitions.add(new Petition("inspect-17", "INSPECT", 4, "6600ab76-3", LocalDate.parse("2025-08-04")));
        petitions.add(new Petition("inspect-18", "INSPECT", 3, "12a13228-0", LocalDate.parse("2025-08-04")));
        petitions.add(new Petition("inspect-19", "INSPECT", 9, "51ed516f-a", LocalDate.parse("2025-08-04")));
        petitions.add(new Petition("inspect-20", "INSPECT", 8, "11b553eb-b", LocalDate.parse("2025-08-04")));

        // Petitions with today's date
        petitions.add(new Petition("1a1a1a1a-1", "RETURN", 9, "11b553eb-b", LocalDate.now()));
        petitions.add(new Petition("2b2b2b2b-2", "RETURN", 9, "6600ab76-3", LocalDate.now()));
        petitions.add(new Petition("3c3c3c3c-3", "RETURN", 9, "12a13228-0", LocalDate.now()));
        //Petition with more than 3 days
        petitions.add(new Petition("4d4d4d4d-4", "LEND", 9, "1940136a-2", LocalDate.now().minusDays(4)));
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
