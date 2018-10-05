package example.servicebase;

public class ServiceException extends RuntimeException {
    public ServiceException(Throwable t) {
        super(t);
    }
}
