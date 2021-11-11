package io.apicurio.bc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BcBlock {

    private int index;
    private String hash;
    private String previousHash;
    private long timestamp;
    private String data;

    private int difficulty;
    private int minterBalance;
    private String minterAddress;

}
