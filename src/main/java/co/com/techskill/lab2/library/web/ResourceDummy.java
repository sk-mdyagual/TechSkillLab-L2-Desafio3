package co.com.techskill.lab2.library.web;

import co.com.techskill.lab2.library.service.IOrchestratorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/dummy")
public class ResourceDummy {
    private final IOrchestratorService orchestratorServiceDummy;

    public ResourceDummy(IOrchestratorService orchestratorService, IOrchestratorService orchestratorServiceDummy) {
        this.orchestratorServiceDummy = orchestratorServiceDummy;
    }

    @GetMapping(name="/start-dummy",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> start(){
        return orchestratorServiceDummy.orchestrate();
    }
}