package aplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class BankerServer   {

    public static void main(String[] args) {
        SpringApplication.run(BankerServer.class, args);
    }

}
