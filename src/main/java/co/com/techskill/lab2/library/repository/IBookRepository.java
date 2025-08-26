package co.com.techskill.lab2.library.repository;

import co.com.techskill.lab2.library.domain.entity.Book;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Profile("mongo")
public interface IBookRepository extends ReactiveMongoRepository<Book, String> {
    Mono<Book> findByBookId(String bookId);

    /*
Modelo de ejecución
- Síncrono (MongoDB driver clásico)
Cada operación bloquea el hilo hasta recibir la respuesta.
Necesitas más hilos para soportar más peticiones concurrentes.
El thread pool crece con la carga, lo que aumenta el consumo de memoria y CPU.

- Reactivo (ReactiveMongoDB)
Escala mejor con menos overhead, aprovechando el event loop.
Cada operación devuelve inmediatamente un Mono o Flux y el hilo queda libre para atender otras solicitudes.
Puedes manejar miles de conexiones concurrentes con pocos hilos.

Concurrencia y escalabilidad
Síncrono
A 500 peticiones concurrentes, si cada consulta tarda 200 ms, tus hilos están ocupados
sin hacer nada útil mientras esperan la respuesta.

Reactivo
Esos 500 clientes pueden ser atendidos con un puñado de hilos, porque mientras la consulta
espera datos de Mongo, el hilo atiende otra petición.

Impacto: Esto se nota especialmente en microservicios que realizan muchas I/O externas (como DB o APIs)
y deben mantener alta capacidad de respuesta con recursos limitados.*/
}
