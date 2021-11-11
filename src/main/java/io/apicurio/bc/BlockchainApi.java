package io.apicurio.bc;

public interface BlockchainApi {

    public BcBlock[] getBlockchain();

    public BcBlock generateNextBlock(String data);

    public void replaceChain(BcBlock[] blockchain);

}
