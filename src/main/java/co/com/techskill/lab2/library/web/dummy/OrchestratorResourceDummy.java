package co.com.techskill.lab2.library.web.dummy;

import co.com.techskill.lab2.library.service.IOrchestratorService;
import co.com.techskill.lab2.library.service.IOrchestratorServiceDummy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/orchestratedummy")
public class OrchestratorResourceDummy {
    private final IOrchestratorServiceDummy orchestratorServiceDummy;

    public OrchestratorResourceDummy(IOrchestratorServiceDummy orchestratorService) {
        this.orchestratorServiceDummy = orchestratorService;
    }


    @GetMapping(name="/start",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> start(){
        return orchestratorServiceDummy.orchestrate();
    }

}
