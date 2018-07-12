package net.schowek.xis.example;

import net.schowek.xis.spring.postpones.PostponedMethodInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final HelloService helloService;
    private final PostponedMethodInvoker postponedMethodInvoker;

    @Autowired
    public HelloController(HelloService helloService, PostponedMethodInvoker postponedMethodInvoker) {
        this.helloService = helloService;
        this.postponedMethodInvoker = postponedMethodInvoker;
    }

    @GetMapping("/hello/{name}")
    public void hello(@PathVariable("name") String name) {
        helloService.hello(new Greeting(name));
    }

    @GetMapping("/run")
    public void run() {
        postponedMethodInvoker.invokeQueuedMethods();
    }
}
