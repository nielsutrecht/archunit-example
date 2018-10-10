package example.fooservice.repository;

import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class FooRepository {
    private AtomicInteger fooCounter = new AtomicInteger();

    public int nextFoo() {
        return fooCounter.incrementAndGet();
    }
}
