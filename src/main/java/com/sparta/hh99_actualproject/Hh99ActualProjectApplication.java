package com.sparta.hh99_actualproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableAspectJAutoProxy
@EnableJpaAuditing
@SpringBootApplication
public class Hh99ActualProjectApplication {

    @PostConstruct
    public void started() {
        // timezone seoul 셋팅
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
    public static void main(String[] args) {
        SpringApplication.run(Hh99ActualProjectApplication.class, args);
    }

}
