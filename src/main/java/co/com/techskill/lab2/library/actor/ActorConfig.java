package co.com.techskill.lab2.library.actor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ActorConfig {
    @Bean
    public InspectActor inspectActor() {
        return new InspectActor();
    }

    @Bean
    public List<Actor> actors(LendActor lendActor, ReturnActor returnActor, InspectActor inspectActor) {
        return List.of(lendActor, returnActor, inspectActor);
    }
}