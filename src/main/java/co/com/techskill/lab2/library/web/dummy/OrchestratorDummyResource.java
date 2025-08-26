package co.com.techskill.lab2.library.web.dummy;

import co.com.techskill.lab2.library.service.dummy.OrchestratorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/orchestratedummy")
public class OrchestratorDummyResource {

    private final OrchestratorService orchestratorService;

    public OrchestratorDummyResource(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @GetMapping(name = "/start",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> start() {
        return orchestratorService.orchestrate();
    }
}
