package co.com.techskill.lab2.library.web;

import co.com.techskill.lab2.library.service.IOrchestratorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/orchestrate")
public class OrchestratorResource {
    private final IOrchestratorService orchestratorService;

    public OrchestratorResource(IOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @GetMapping(value = "/start", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> start() {
        return orchestratorService.orchestrate();
    }
}
