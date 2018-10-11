package example.barservice;

import example.servicebase.ServiceBase;

public class BarApplication extends ServiceBase {
    public BarApplication(int port) {
        super(port);
    }


    public void start() {
        start("/foo", ex -> write("bar", ex));
    }

    public static void main(String... argv) {
        new BarApplication(8081).start();
    }
}
