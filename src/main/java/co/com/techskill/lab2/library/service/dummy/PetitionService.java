package co.com.techskill.lab2.library.service.dummy;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class PetitionService {
    private final List<PetitionDTO> petitions = new ArrayList<>();

    BookService bookService = new BookService();

    public PetitionService(){
        petitions.add(new PetitionDTO("09c09cc8-b", "LEND", 5, "6600ab76-3", LocalDate.parse("2025-08-27")));
        petitions.add(new PetitionDTO("2f5fca21-b", "RETURN", 7, "12a13228-0", LocalDate.parse("2025-08-27")));
        petitions.add(new PetitionDTO("4c9ef769-9", "LEND", 7, "51ed516f-a", LocalDate.parse("2025-08-27")));
        petitions.add(new PetitionDTO("5b2dae36-f", "LEND", 3, "51ed516f-a", LocalDate.parse("2025-08-27")));
        petitions.add(new PetitionDTO("ad4801f0-9", "RETURN", 5, "51ed516f-a", LocalDate.parse("2025-08-27")));
        petitions.add(new PetitionDTO("9cc825c1-7", "RETURN", 7, "12a13228-0", LocalDate.parse("2025-08-27")));
        petitions.add(new PetitionDTO("d5120259-4", "LEND", 4, "11b553eb-b", LocalDate.parse("2025-08-30")));
        petitions.add(new PetitionDTO("09ef7d35-d", "RETURN", 4, "297c17d8-4", LocalDate.parse("2025-08-30")));
        petitions.add(new PetitionDTO("0e6a31b1-f", "RETURN", 4, "6600ab76-3", LocalDate.parse("2025-08-20")));
        petitions.add(new PetitionDTO("905dfc53-7", "LEND", 5, "6600ab76-3", LocalDate.parse("2025-08-20")));
        petitions.add(new PetitionDTO("4ebc9aa6-f", "RETURN", 7, "3c24c2fa-3", LocalDate.parse("2025-08-20")));
        petitions.add(new PetitionDTO("6d7e3b2c-5", "LEND", 4, "eb25c2d4-7", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("2a6214f1-c", "RETURN", 3, "eb25c2d4-7", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("8595a9b7-7", "RETURN", 7, "51ed516f-a", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("890fd155-0", "LEND", 2, "51ed516f-a", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("2da99667-d", "LEND", 4, "1940136a-2", LocalDate.parse("2025-08-22")));
        petitions.add(new PetitionDTO("cbfdd0aa-c", "RETURN", 7, "1940136a-2", LocalDate.parse("2025-08-25")));
        petitions.add(new PetitionDTO("0ff09c9e-5", "LEND", 6, "11b553eb-b", LocalDate.parse("2025-08-25")));
        petitions.add(new PetitionDTO("86084e60-e", "RETURN", 7, "11b553eb-b", LocalDate.parse("2025-08-25")));
        petitions.add(new PetitionDTO("742330cf-0", "LEND", 6, "12a13228-0", LocalDate.parse("2025-08-25")));
        petitions.add(new PetitionDTO("7589dda0-0", "RETURN", 10, "6600ab76-3", LocalDate.parse("2025-08-26")));
        petitions.add(new PetitionDTO("85d507b5-1", "LEND", 9, "6600ab76-3", LocalDate.parse("2025-08-26")));
        petitions.add(new PetitionDTO("1c67c444-2", "RETURN", 8, "6600ab76-3", LocalDate.parse("2025-08-25")));
        petitions.add(new PetitionDTO("d5380ce2-3", "LEND", 9, "6600ab76-3", LocalDate.parse("2025-08-25")));
        petitions.add(new PetitionDTO("58aa0cbf-4", "LEND", 10, "6600ab76-3", LocalDate.parse("2025-08-26")));
        petitions.add(new PetitionDTO("836cbf00-5", "RETURN", 9, "6600ab76-3", LocalDate.parse("2025-08-26")));
        petitions.add(new PetitionDTO("1f634457-6", "RETURN", 8, "6600ab76-3", LocalDate.parse("2025-08-26")));
        petitions.add(new PetitionDTO("09c09cc8-b", "INSPECT", 5, "6600ab76-3", LocalDate.parse("2025-08-27")));
        petitions.add(new PetitionDTO("2f5fca21-b", "INSPECT", 7, "12a13228-0", LocalDate.parse("2025-08-27")));
        petitions.add(new PetitionDTO("4c9ef769-9", "INSPECT", 6, "51ed516f-a", LocalDate.parse("2025-08-27")));
        petitions.add(new PetitionDTO("58aa0cbf-4", "INSPECT", 10, "6600ab76-3", LocalDate.parse("2025-08-26")));
        petitions.add(new PetitionDTO("836cbf00-5", "INSPECT", 9, "6600ab76-3", LocalDate.parse("2025-08-26")));
        petitions.add(new PetitionDTO("1f634457-6", "INSPECT", 8, "6600ab76-3", LocalDate.parse("2025-08-26")));

    }

    public Flux<PetitionDTO> dummyFindAll(){
        return Flux.fromIterable(petitions);
    }

    public Mono<PetitionDTO> dummyFindById(String id){
        return Mono.justOrEmpty(
                petitions.stream()
                        .filter(petitionDTO -> petitionDTO.getPetitionId().equals(id))
                        .findFirst()
        );
    }

    public Flux<PetitionDTO> dummyHighPriority(){
        return dummyFindAll()
                .filter(p -> p.getPriority() != null && p.getPriority() >= 7);
                //.sort((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()));
    }

    public Mono<String> processReturnPetitions(String id) {
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("petitionService");

        PetitionDTO petition = petitions.stream()
                .filter(p -> p.getPetitionId().equals(id))
                .findFirst()
                .orElse(null);
        if (petition == null) {
            return Mono.just("Petition ID not found");
        }
        return Mono.just(petition)
                .flatMap(p -> {
                    if (!petition.getType().equals("RETURN")) {
                        return Mono.error(new RuntimeException("Petition type is not RETURN"));
                    }

                    if (petition.getSentAt().isBefore(threeDaysAgo)) {
                        return Mono.error(new RuntimeException("Petition was sent more than 3 days ago"));
                    }
                    if (Math.random() < 0.9) {
                        return Mono.error(new RuntimeException("OOops, we encountered random failure"));
                    }
                    return bookService.dummyFindById(p.getBookId());
                })
                .map(book -> "Petition approved for book: " + book.getBookId())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .timeout(Duration.ofSeconds(2))
                .retry(3)
                .onErrorResume(e -> Mono.just("Petition failed for book: " + petition.getBookId() + " - " + e.getMessage()));
    }


}

