package io.apicurio.bc.wallet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.apicurio.bc.BcBlock;

@ApplicationScoped
public class WalletApi {

    @ConfigProperty(name = "wallet.address")
    String walletAddress;

    public String getPublicAddress() {
        return walletAddress;
    }

    //pretty dumb how it works I know
    public int getBalance(BcBlock[] chain) {
        for (int i = chain.length ; i >= 0 ; i-- ) {
            BcBlock block = chain[i];
            if (block.getMinterAddress().equals(getPublicAddress())) {
                return block.getMinterBalance();
            }
        }
        return 0;
    }

}
