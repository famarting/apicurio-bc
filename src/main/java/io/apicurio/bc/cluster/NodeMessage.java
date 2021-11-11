package io.apicurio.bc.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeMessage {

    private NodeMessageType type;
    private byte[] data;

}
