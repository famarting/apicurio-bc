package io.apicurio.bc.cluster;

public enum NodeMessageType {

    REQUEST_BLOCKCHAIN(0),
    RESPONSE_BLOCKCHAIN(1);

    private int index;

    private NodeMessageType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
