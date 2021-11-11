package io.apicurio.bc.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import io.apicurio.bc.BcBlock;
import io.apicurio.bc.BlockchainApi;
import io.apicurio.bc.cluster.ClusterApi;

public class ApicurioBcApiImpl implements ApicurioBcApi {

    @Inject
    BlockchainApi bc;

    @Inject
    ClusterApi cluster;

    @Override
    public Response getBlockchain() {
        return Response.ok(bc.getBlockchain()).build();
    }

    @Override
    public BcBlock generateNextBlock(String data) {
        return bc.generateNextBlock(data);
    }

    @Override
    public List<String> getPeers() {
        return cluster.getPeers();
    }

    @Override
    public void addPeer(String peer) {
        cluster.addPeer(peer);
    }

}
