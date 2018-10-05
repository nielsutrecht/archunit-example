package example.fooservice.repository;

import java.util.concurrent.atomic.AtomicInteger;

public class FooRepository {
    private AtomicInteger fooCounter = new AtomicInteger();

    public int nextFoo() {
        return fooCounter.incrementAndGet();
    }
}
