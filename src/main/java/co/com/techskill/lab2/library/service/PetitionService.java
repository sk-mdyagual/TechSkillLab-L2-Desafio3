package co.com.techskill.lab2.library.service;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;


public class PetitionService {
    private final List<PetitionDTO> petitions = List.of(
            new PetitionDTO("1", "LEND", 7, "bookA", LocalDate.now()),
            new PetitionDTO("2", "RETURN", 5, "bookB", LocalDate.now()),
            new PetitionDTO("3", "LEND", 9, "bookC", LocalDate.now()),
            new PetitionDTO("4", "RETURN", 3, "bookD", LocalDate.now()),
            new PetitionDTO("5", "LEND", 4, "bookE", LocalDate.now()),
            new PetitionDTO("6", "RETURN", 6, "bookF", LocalDate.now()),
            new PetitionDTO("7", "LEND", 8, "bookG", LocalDate.now()),
            new PetitionDTO("8", "RETURN", 2, "bookG", LocalDate.now()),
            new PetitionDTO("9", "LEND", 5, "bookH", LocalDate.now()),
            new PetitionDTO("10", "RETURN", 7, "bookI", LocalDate.now()),
            new PetitionDTO("11", "LEND", 6, "bookJ", LocalDate.now()),
            new PetitionDTO("12", "RETURN", 4, "bookK", LocalDate.now()),
            new PetitionDTO("13", "LEND", 3, "bookL", LocalDate.now()),
            new PetitionDTO("14", "RETURN", 8, "bookM", LocalDate.now()),
            new PetitionDTO("15", "LEND", 2, "bookN", LocalDate.now()),
            new PetitionDTO("16", "LEND", 9, "bookO", LocalDate.now()),
            new PetitionDTO("17", "RETURN", 3, "bookP", LocalDate.now()),
            new PetitionDTO("18", "LEND", 4, "bookQ", LocalDate.now()),
            new PetitionDTO("19", "RETURN", 6, "bookR", LocalDate.now()),
            new PetitionDTO("20", "LEND", 8, "bookS", LocalDate.now()),
            new PetitionDTO("21", "RETURN", 2, "bookT", LocalDate.now()),
            new PetitionDTO("22", "LEND", 5, "bookU", LocalDate.now()),
            new PetitionDTO("23", "RETURN", 7, "bookV", LocalDate.now()),
            new PetitionDTO("24", "LEND", 6, "bookW", LocalDate.now()),
            new PetitionDTO("25", "RETURN", 4, "bookX", LocalDate.now()),
            new PetitionDTO("26", "LEND", 3, "bookY", LocalDate.now()),
            new PetitionDTO("27", "RETURN", 8, "bookZ", LocalDate.now()),
            new PetitionDTO("28", "LEND", 2, "bookN", LocalDate.now()));

    public Flux<String> actorOrquestator() {
        return Flux.fromIterable(petitions)
                .groupBy(PetitionDTO::getType)
                .flatMap(group -> {
                    String type = group.key();
                    if ("LEND".equals(type)) {
                        return group
                                .sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                                .map(p -> "Actor LEND procesando -> ID: " + p.getPetitionId()
                                        + " | Libro: " + p.getBookId()
                                        + " | Prioridad: " + p.getPriority())
                                .delayElements(Duration.ofMillis(500));
                    } else {
                        return group
                                .map(p -> "Actor RETURN procesando -> ID: " + p.getPetitionId()
                                        + " | Libro: " + p.getBookId()
                                        + " | Prioridad: " + p.getPriority())
                                .delayElements(Duration.ofMillis(300));
                    }
                })
                .limitRate(1);
    }
}
