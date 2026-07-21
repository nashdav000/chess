package server;

import chess.*;
import dataaccess.*;
import service.GameService;
import service.UserService;

public class ServerMain {
    public static void main(String[] args) {
        try{

        var server = new Server().run(8080);

        System.out.println("♕ 240 Chess Server");
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error: Database Malfunction");}
        }
    }