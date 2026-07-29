package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        String url = args.length >= 1 ? args[0] : "http://localhost:8080";

        try {
            new PreloginClient(url).run();

        } catch (Exception e) {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }
    }
}
