package co.com.techskill.lab2.library;

import co.com.techskill.lab2.library.service.PetitionService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
@OpenAPIDefinition
public class LibraryApplication {

	public static void main(String[] args) {

        PetitionService service = new PetitionService();
        Flux<String> flujo = service.actorOrquestator();

        flujo.doOnNext(System.out::println)
                .blockLast();

        System.out.println("Fin");
        //SpringApplication.run(LibraryApplication.class, args);
	}

}
