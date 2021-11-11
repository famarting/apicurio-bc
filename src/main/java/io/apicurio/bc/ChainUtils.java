package io.apicurio.bc;

import org.apache.commons.codec.digest.DigestUtils;

public class ChainUtils {

    public static BcBlock genesisBlock(int initialAmount, String minterAddress) {
        BcBlock b = new BcBlock(0, null, null, System.currentTimeMillis(), "genesis", 0, initialAmount, minterAddress);
        String hash = calculateBlockHash(b);
        b.setHash(hash);
        return b;
    }

    public static String calculateBlockHash(BcBlock block) {
        String valueToHash = String.valueOf(block.getIndex())
                    + block.getPreviousHash()
                    + block.getTimestamp()
                    + block.getData()
                    + block.getMinterBalance()
                    + block.getMinterAddress();
        return DigestUtils.sha256Hex(valueToHash);
    }
}
