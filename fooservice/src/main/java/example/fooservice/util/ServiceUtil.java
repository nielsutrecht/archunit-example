package example.fooservice.util;

import example.fooservice.domain.Foo;

public class ServiceUtil {
    private ServiceUtil() {}

    public static Foo createFoo(int next) {
        return new Foo("Foo #" + next);
    }
}
