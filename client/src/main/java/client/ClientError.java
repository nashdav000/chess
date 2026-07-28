package client;

public class ClientError extends RuntimeException {
    public ClientError(String message) {
        super(message);
    }
}
