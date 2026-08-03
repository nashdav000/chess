package server.websocket;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    

    public final ConcurrentHashMap<Session, String> connections = new ConcurrentHashMap<>();

    public void add(String gameID, Session session) {connections.put(session, gameID);}

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcastOthers(Session excludedSession, ServerMessage message) throws IOException {
        String msg = new Gson().toJson(message);
        String gameID = connections.get(excludedSession);

        for (Session s : connections.keySet()){
            if (s.isOpen()){
                if (Objects.equals(connections.get(s), gameID) && s != excludedSession){
                    s.getRemote().sendString(msg);
                }
            }

        }
    }

    public void broadcastSelf(Session session, ServerMessage message) throws IOException {
        String msg = new Gson().toJson(message);

        if (session.isOpen()) {
            session.getRemote().sendString(msg);
        }
    }
}
