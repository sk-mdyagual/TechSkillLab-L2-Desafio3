package co.com.techskill.lab2.library.web.dummy;

import co.com.techskill.lab2.library.service.IOrchestratorService;
import co.com.techskill.lab2.library.service.dummy.OrchestratorService;
import co.com.techskill.lab2.library.service.dummy.PetitionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/dummy/orchestrate")
public class OrchestratorDummyResource {
    private final OrchestratorService orchestratorService;
    private final PetitionService petitionService;

    public OrchestratorDummyResource(OrchestratorService orchestratorService, PetitionService petitionService) {
        this.orchestratorService = orchestratorService;
        this.petitionService = petitionService;
    }

    @GetMapping(name="/start",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> start(){
        return orchestratorService.dummyOrchestrate();
    }
}