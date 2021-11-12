package io.apicurio.bc.cluster;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.apicurio.bc.BcBlock;
import io.apicurio.bc.BlockchainApi;
import io.vertx.core.impl.ConcurrentHashSet;

@ServerEndpoint("/nodes")
@ApplicationScoped
public class NodesServer implements ClusterApi {

    private ObjectMapper mapper = new ObjectMapper();

    private Set<Session> sessions = new ConcurrentHashSet<>();

    private Set<String> peers = new HashSet<>();

    @Inject
    BlockchainApi blockchain;

    @Override
    public List<String> getPeers() {
        return new ArrayList<>(peers);
    }

    @Override
    public void addPeer(String peer) {
        // TODO Auto-generated method stub
        // https://quarkus.io/guides/websockets
        try {
            Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, URI.create(peer+"/nodes"));
            peers.add(peer);
        } catch ( DeploymentException | IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void bradcastBlockchain() {
        sessions.forEach(s -> {
            try {
                sendBlockchainMessage(s);
            } catch ( JsonProcessingException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }



    @ClientEndpoint
    public class Client {

        @OnOpen
        public void onOpen(Session session) throws Exception {
            sessions.add(session);
            session.getAsyncRemote()
                .sendText(mapper.writeValueAsString(new NodeMessage(NodeMessageType.REQUEST_BLOCKCHAIN, null)));
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
            handleOnMessage(message, session);
        }

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
        handleOnMessage(message, session);
    }

    private void handleOnMessage(String message, Session session) throws JsonProcessingException, JsonMappingException, IOException, JsonParseException {
        NodeMessage nm = mapper.readValue(message, NodeMessage.class);

        switch ( nm.getType() ) {
            case REQUEST_BLOCKCHAIN:
                // reply with the entire blockchain encoded as json
                sendBlockchainMessage(session);
                break;
            case RESPONSE_BLOCKCHAIN:
                // replace our current blockchain with this one
                blockchain.replaceChain(mapper.readValue(nm.getData(), BcBlock[].class));
                break;
            default:
                break;
        }
    }

    private void sendBlockchainMessage(Session session) throws JsonProcessingException {
        session.getAsyncRemote()
            .sendText(mapper.writeValueAsString(new NodeMessage(NodeMessageType.RESPONSE_BLOCKCHAIN, mapper.writeValueAsBytes(blockchain.getBlockchain()))));
    }

}