package net.schowek.xis.example;

import net.schowek.xis.spring.postpones.PostponedOperationsInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final HelloService helloService;
    private final PostponedOperationsInvoker postponedOperationsInvoker;

    @Autowired
    public HelloController(HelloService helloService, PostponedOperationsInvoker postponedOperationsInvoker) {
        this.helloService = helloService;
        this.postponedOperationsInvoker = postponedOperationsInvoker;
    }

    @GetMapping("/hello/{name}")
    public void hello(@PathVariable("name") String name) {
        helloService.hello(new Greeting(name));
    }

    @GetMapping("/run")
    public void run() {
        postponedOperationsInvoker.invokeQueued();
    }
}
