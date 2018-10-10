package example.fooservice;

import example.fooservice.repository.FooRepository;
import example.fooservice.service.FooService;
import example.servicebase.ServiceBase;

public class FooApplication extends ServiceBase {
    private final FooService service;

    public FooApplication(int port) {
        super(port);

        this.service = new FooService(new FooRepository());
    }

    public void start() {
        start("/foo", ex -> writeObject(service.getFoo(), ex));
    }

    public static void main(String... argv) {
        new FooApplication(8080).start();
    }
}
