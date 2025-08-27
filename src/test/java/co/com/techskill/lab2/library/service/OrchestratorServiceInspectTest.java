package co.com.techskill.lab2.library.service;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.actor.InspectActor;
import co.com.techskill.lab2.library.domain.entity.Book;
import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.repository.IBookRepository;
import co.com.techskill.lab2.library.repository.IPetitionRepository;
import co.com.techskill.lab2.library.service.impl.OrchestratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

class OrchestratorServiceInspectTest {

    @Mock
    private IPetitionRepository petitionRepository;

    @Mock
    private IBookRepository bookRepository;

    private OrchestratorServiceImpl orchestratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        InspectActor inspectActor = new InspectActor(bookRepository);
        List<Actor> actors = List.of(inspectActor);
        
        orchestratorService = new OrchestratorServiceImpl(petitionRepository, actors);
    }

    @Test
    void testOrchestratorFiltersInspectPetitionsByPriority() {
        // Arrange
        Petition lowPriorityPetition = new Petition("INSPECT001", "INSPECT", 5, "BOOK001", LocalDate.now());
        Petition highPriorityPetition = new Petition("INSPECT002", "INSPECT", 8, "BOOK002", LocalDate.now());
        Petition veryHighPriorityPetition = new Petition("INSPECT003", "INSPECT", 10, "BOOK003", LocalDate.now());
        
        Book book1 = new Book("BOOK001", "978-111", "Book 1", 1, true);
        Book book2 = new Book("BOOK002", "978-222", "Book 2", 1, true);
        Book book3 = new Book("BOOK003", "978-333", "Book 3", 1, true);

        when(petitionRepository.findAll()).thenReturn(
            Flux.just(lowPriorityPetition, highPriorityPetition, veryHighPriorityPetition)
        );
        
        when(bookRepository.findByBookId("BOOK001")).thenReturn(Mono.just(book1));
        when(bookRepository.findByBookId("BOOK002")).thenReturn(Mono.just(book2));
        when(bookRepository.findByBookId("BOOK003")).thenReturn(Mono.just(book3));

        // Act & Assert
        StepVerifier.create(orchestratorService.orchestrate())
                .expectNext("[INSPECT] petition for book: BOOK003 with priority 10")  // Ordenado por prioridad descendente
                .expectNext("[INSPECT] petition for book: BOOK002 with priority 8")
                .verifyComplete();
    }

    @Test
    void testOrchestratorIgnoresLowPriorityInspectPetitions() {
        // Arrange
        Petition lowPriorityPetition1 = new Petition("INSPECT001", "INSPECT", 3, "BOOK001", LocalDate.now());
        Petition lowPriorityPetition2 = new Petition("INSPECT002", "INSPECT", 6, "BOOK002", LocalDate.now());
        Petition highPriorityPetition = new Petition("INSPECT003", "INSPECT", 9, "BOOK003", LocalDate.now());
        
        Book book3 = new Book("BOOK003", "978-333", "Book 3", 1, true);

        when(petitionRepository.findAll()).thenReturn(
            Flux.just(lowPriorityPetition1, lowPriorityPetition2, highPriorityPetition)
        );
        
        when(bookRepository.findByBookId("BOOK003")).thenReturn(Mono.just(book3));

        // Act & Assert
        StepVerifier.create(orchestratorService.orchestrate())
                .expectNext("[INSPECT] petition for book: BOOK003 with priority 9")
                .verifyComplete();
    }
}
