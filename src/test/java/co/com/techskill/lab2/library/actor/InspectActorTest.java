package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Book;
import co.com.techskill.lab2.library.domain.entity.Petition;
import co.com.techskill.lab2.library.repository.IBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class InspectActorTest {

    @Mock
    private IBookRepository bookRepository;

    private InspectActor inspectActor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inspectActor = new InspectActor(bookRepository);
    }

    @Test
    void testSupportsInspectType() {
        // Arrange & Act & Assert
        assertTrue(inspectActor.supports("INSPECT"));
        assertFalse(inspectActor.supports("LEND"));
        assertFalse(inspectActor.supports("RETURN"));
    }

    @Test
    void testHandleInspectPetition() {
        // Arrange
        Petition petition = new Petition("INSPECT001", "INSPECT", 8, "BOOK001", LocalDate.now());
        Book book = new Book("BOOK001", "978-123456", "Test Book", 5, true);

        when(bookRepository.findByBookId("BOOK001")).thenReturn(Mono.just(book));

        // Act & Assert
        StepVerifier.create(inspectActor.handle(petition))
                .expectNext("[INSPECT] petition for book: BOOK001 with priority 8")
                .verifyComplete();
    }

    @Test
    void testHandleInspectPetitionWithHighPriority() {
        // Arrange
        Petition petition = new Petition("INSPECT002", "INSPECT", 10, "BOOK002", LocalDate.now());
        Book book = new Book("BOOK002", "978-789012", "High Priority Book", 3, true);

        when(bookRepository.findByBookId("BOOK002")).thenReturn(Mono.just(book));

        // Act & Assert
        StepVerifier.create(inspectActor.handle(petition))
                .expectNext("[INSPECT] petition for book: BOOK002 with priority 10")
                .verifyComplete();
    }
}
