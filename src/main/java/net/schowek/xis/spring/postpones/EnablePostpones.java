package net.schowek.xis.spring.postpones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAspectJAutoProxy()
@Import(PostponesConfiguration.class)
public @interface EnablePostpones {
    Class<? extends InvocationRepository> repository();
    boolean autoInvoke() default true;
    long sleepTime() default 100;
}
