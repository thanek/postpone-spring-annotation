package net.schowek.xis.example;

import net.schowek.xis.spring.postpones.MethodInvocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    private final HelloService helloService;
    private final MethodInvocator methodInvocator;

    public HelloController(HelloService helloService, MethodInvocator methodInvocator) {
        this.helloService = helloService;
        this.methodInvocator = methodInvocator;
    }

    @GetMapping("/hello/{name}")
    public void hello(@PathVariable("name") String name) {
        helloService.hello(name);
    }

    @GetMapping("/run")
    public void run() {
        try {
            methodInvocator.invokeQueuedMethods();
        } catch (Exception e) {
            logger.error("Invoking error", e);
        }
    }
}
