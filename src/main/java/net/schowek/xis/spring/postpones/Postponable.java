package net.schowek.xis.spring.postpones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.scheduling.annotation.Async;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Postponable {
    Class<? extends InvocationRepository> repository();
}
