package io.apicurio.bc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.apicurio.bc.cluster.ClusterApi;
import io.apicurio.bc.wallet.WalletApi;

@ApplicationScoped
public class InMemoryBlockchain implements BlockchainApi {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    WalletApi wallet;
    @Inject
    ClusterApi cluster;

    private List<BcBlock> chain;

    @PostConstruct
    public void init() {
        chain = new CopyOnWriteArrayList<BcBlock>();
        //start granting 100 coins to this node
        chain.add(ChainUtils.genesisBlock(100, wallet.getPublicAddress()));
    }

    private BcBlock getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    @Override
    public BcBlock[] getBlockchain() {
        return chain.toArray(new BcBlock[chain.size()]);
    }

    @Override
    public BcBlock generateNextBlock(String data) {

        int difficulty = 0; //TODO explore this
        BcBlock latestBlock = getLatestBlock();
        int newIndex = latestBlock.getIndex();

        BcBlock block = findBlock(newIndex, latestBlock.getHash(), data, difficulty);

        if (addBlockToChain(block)) {
            cluster.bradcastBlockchain();
            return block;
        } else {
            return null;
        }
    }

    @Override
    public void replaceChain(BcBlock[] blockchain) {

        //replace only if the incoming chain is longer than our current chain
        if (blockchain.length <= this.chain.size()) {
            logger.info("not replacing chain because it's not bigger");
            return;
        }

        //validate hashes in each block
        for (int i = 0; i < blockchain.length; i++) {

            if (i != 0) {
                if (!isValidBlock(blockchain[i], blockchain[i - 1])) {
                    logger.info("chain contains invalid block, not replacing");
                    return;
                }
            }

            // process transactions? if we have?
        }

        this.chain = new CopyOnWriteArrayList<BcBlock>(blockchain);

        //broadcastLatest
        cluster.bradcastBlockchain();

    }

    private boolean addBlockToChain(BcBlock block) {
        if (isValidBlock(block, getLatestBlock())) {
            chain.add(block);
            return true;
        }
        return false;
    }

    private boolean isValidBlock(BcBlock block, BcBlock previousBlock) {
        // verify hashes, timestamps, ... make sense

        if (previousBlock.getIndex() + 1 != block.getIndex()) {
            logger.info("invalid index");
            return false;
        } else if (!previousBlock.getHash().equals(block.getPreviousHash())) {
            logger.info("invalid previoushash");
            return false;
        } else if (block.getTimestamp() <= previousBlock.getTimestamp()) {
            logger.info("invalid timestamp");
            return false;
        } else if (!ChainUtils.calculateBlockHash(block).equals(block.getHash())) {
            return false;
        }
        return true;
    }

    //this is where proof of stake is implemented
    private BcBlock findBlock(int index, String previousHash, String data, int difficulty) {
        long pastTimestamp = 0;
        while (true) {
            long timestamp = System.currentTimeMillis();
            if(pastTimestamp != timestamp) {
                String address = wallet.getPublicAddress();
                int balance = wallet.getBalance(getBlockchain());
                BcBlock temporaryBlock = new BcBlock(index, null, previousHash, timestamp, data, difficulty, balance, address);
                String hash = ChainUtils.calculateBlockHash(temporaryBlock);
                if (isBlockStakingValid(previousHash, address, timestamp, balance, difficulty, index)) {
                    return new BcBlock(index, hash, previousHash, timestamp, data, difficulty, balance, address);
                }
                pastTimestamp = timestamp;
            }
        }
    }

    // This function is used for proof of stake
    // Based on `SHA256(prevhash + address + timestamp) <= 2^256 * balance / diff`
    // Cf https://blog.ethereum.org/2014/07/05/stake/
    private boolean isBlockStakingValid(String previousHash, String address, long timestamp, int balance, int difficulty, int index) {
        difficulty = difficulty + 1;

//        // Allow minting without coins for a few blocks
//        if(index <= mintingWithoutCoinIndex) {
//            balance = balance + 1;
//        }

        double balanceOverDifficulty = (Math.pow(2, 256) * balance) / difficulty;
        String stakingHash = DigestUtils.sha256Hex(previousHash + address + String.valueOf(timestamp));

        //        BigInteger decimalStakingHash = new BigInteger(stakingHash);
        Long decimalStakingHash = Long.parseLong(stakingHash, 16);
//        const decimalStakingHash = new BigNumber(stakingHash, 16);

//        const difference = balanceOverDifficulty.minus(decimalStakingHash).toNumber();
//        return difference >= 0;

        return decimalStakingHash.doubleValue() <= balanceOverDifficulty;
    }
}
