package net.schowek.xis.spring.postpones;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.Instant.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PostponedMethodInvocationTests {
    private AnnotationConfigApplicationContext context;

    @Before
    public void setup() {
        context = new AnnotationConfigApplicationContext(TestConfiguration.class);
    }

    @After
    public void cleanup() {
        context.close();
    }

    @Test
    public void shouldProxyOnlyBeansWithAnnotatedMethods() {
        Service service = context.getBean(Service.class);
        Foo foo = context.getBean(Foo.class);

        assertTrue(AopUtils.isAopProxy(service));
        assertFalse(AopUtils.isAopProxy(foo));
    }

    @Test
    public void shouldScanForAnnotatedMethods() {
        PostponedMethodsScanner postponedMethodsScanner = context.getBean(PostponedMethodsScanner.class);

        assertNotNull(postponedMethodsScanner.get("Test::doStuff"));
        assertNotNull(postponedMethodsScanner.get(
                "net.schowek.xis.spring.postpones.PostponedMethodInvocationTests.Service::doOtherStuff([])"));
    }

    @Test
    public void shouldPostponeMethodExecution() {
        Service service = context.getBean(Service.class);
        InMemoryRepository repository = context.getBean(InMemoryRepository.class);

        service.doStuff();

        assertFalse(service.isStuffDone());
        assertEquals(1, repository.count());
    }

    @Test
    public void shouldRunPostponedMethods() {
        Service service = context.getBean(Service.class);
        PostponedMethodsScanner postponedMethodsScanner = context.getBean(PostponedMethodsScanner.class);
        InMemoryRepository repository = context.getBean(InMemoryRepository.class);
        repository.add(new Invocation("1", now(), "Test::doStuff", new Object[0]));

        PostponedOperationsInvoker methodInvoker = new PostponedOperationsInvoker(repository, postponedMethodsScanner);
        methodInvoker.invokeQueued();

        assertTrue(service.isStuffDone());
        assertEquals(0, repository.count());
    }

    @Configuration
    @EnablePostpones
    protected static class TestConfiguration {
        @Autowired
        ApplicationContext applicationContext;

        @Bean
        public PostponedMethodsScanner postponedMethodsScanner() {
            return new PostponedMethodsScanner(applicationContext);
        }

        @Bean
        public Service service() {
            return new Service();
        }

        @Bean
        public Foo foo() {
            return new Foo();
        }

        @Bean
        public InvocationRepository invocationRepository() {
            return new InMemoryRepository();
        }
    }

    public static class Service {
        private boolean stuffDone = false;

        @Postponable(repository = InMemoryRepository.class, methodQualifier = "Test::doStuff")
        public void doStuff() {
            stuffDone = true;
        }

        @Postponable(repository = InMemoryRepository.class)
        public void doOtherStuff() {
            stuffDone = true;
        }

        public boolean isStuffDone() {
            return stuffDone;
        }
    }

    private static class Foo {
    }

    private static class InMemoryRepository implements InvocationRepository {
        private final List<Invocation> memory = new ArrayList<>();

        @Override
        public void add(Invocation invocation) {
            memory.add(invocation);
        }

        @Override
        public Optional<Invocation> findFirst() {
            return memory.stream().findFirst();
        }

        @Override
        public void markAsDone(Invocation invocation) {
            memory.remove(invocation);
        }

        public int count() {
            return memory.size();
        }
    }
}
