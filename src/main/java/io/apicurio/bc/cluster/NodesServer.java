package io.apicurio.bc.cluster;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.apicurio.bc.BlockchainApi;
import io.vertx.core.impl.ConcurrentHashSet;

@ServerEndpoint("/nodes")
@ApplicationScoped
public class NodesServer implements ClusterApi {

    private ObjectMapper mapper = new ObjectMapper();

    private Set<Session> sessions = new ConcurrentHashSet<>();

    @Inject
    BlockchainApi blockchain;

    @Override
    public List<String> getPeers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addPeer(String peer) {
        // TODO Auto-generated method stub
        // https://quarkus.io/guides/websockets
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws Exception {

        NodeMessage nm = mapper.readValue(message, NodeMessage.class);

        switch ( nm.getType() ) {
            case REQUEST_BLOCKCHAIN:
                // reply with the entire blockchain encoded as json
                break;
            case RESPONSE_BLOCKCHAIN:
                // replace our current blockchain with this one
                break;
            default:
                break;
        }

//        session.getBasicRemote().sendText(null);

    }

//    private void broadcast(String message) {
//        sessions.forEach(s -> {
//            s.getAsyncRemote().sendObject(message, result ->  {
//                if (result.getException() != null) {
//                    System.out.println("Unable to send message: " + result.getException());
//                }
//            });
//        });
//    }

}