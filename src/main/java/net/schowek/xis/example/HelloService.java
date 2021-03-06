package net.schowek.xis.example;

import net.schowek.xis.spring.postpones.Postponable;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class HelloService {
    private static final Logger logger = getLogger(HelloService.class);

    @Postponable(methodQualifier = "HelloService::hello")
    public void hello(Greeting greeting) {
        logger.info("SAYING HELLO TO {}", greeting);
    }
}
