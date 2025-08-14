package co.com.techskill.lab2.library.repository;

import co.com.techskill.lab2.library.domain.entity.Petition;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IPetitionRepository extends ReactiveMongoRepository<Petition, String> {
    Mono<Petition> findByPetitionId(String id);
    /*
    Manejo de recursos
    Síncrono:
    Más consumo de memoria por thread stacks.
    Más context switching en CPU.
    Mayor riesgo de saturar el thread pool bajo alta carga.

    Reactivo:
    Menos memoria ocupada.
    Menos cambio de contexto.
    Mejor aprovechamiento del CPU, especialmente en entornos cloud o contenedores con recursos limitados.*/
}
