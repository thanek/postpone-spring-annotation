package net.schowek.xis.example;

import java.time.Instant;

public class Greeting {
    private final String name;
    private final Instant greetTime;

    public Greeting(String name) {
        this.name = name;
        this.greetTime = Instant.now();
    }

    public String getName() {
        return name;
    }

    public Instant getGreetTime() {
        return greetTime;
    }

    @Override
    public String toString() {
        return "Greeting{" +
                "name='" + name + '\'' +
                ", greetTime=" + greetTime +
                '}';
    }
}
