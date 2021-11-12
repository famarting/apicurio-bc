package io.apicurio.bc.cluster;

import java.util.List;

public interface ClusterApi {

    public List<String> getPeers();

    public void addPeer(String peer);

    public void bradcastBlockchain();
}
