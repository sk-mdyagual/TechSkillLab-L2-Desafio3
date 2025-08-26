package co.com.techskill.lab2.library.config;

import co.com.techskill.lab2.library.actor.Actor;
import co.com.techskill.lab2.library.actor.LendActor;
import co.com.techskill.lab2.library.actor.ReturnActor;
import co.com.techskill.lab2.library.actor.NewInspectActor;
import co.com.techskill.lab2.library.service.IBookServiceDummy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ActorConfig {
    @Bean
    public List<Actor> actors(IBookServiceDummy bookServiceDummy) {
        return Arrays.asList(
                new LendActor(bookServiceDummy),
                new ReturnActor(bookServiceDummy),
                new NewInspectActor(bookServiceDummy)
        );
    }
}
