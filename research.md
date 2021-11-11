# Apicurio Registry Blockchain hackathon

### Usecases
- none? just for fun
- adding trust to registry storage
- adding trust to registry data migration or data replication
- as the storage for auditing information


### Approaches
- blockchain as a complete storage implementation
- blockchain as a ledger to help with storage implementation, similar to kafkasql but blockchainsql :)
- as something complementary to the sql mode, to add trust to sql storage, maybe using registry storage delegators we have

### Caveats
- requires to be deployed as a cluster or won't provide trust, requires nodes to communicate to each other
- needs proof of something to provide trust, proof of work , proof of stake , proof of space
- needs an API to verify whatever data we store is true with the blockchain, if not what's the point of having a blockchain
- 51% attacks?

### Concepts
Consensus algorithm: proof of work , proof of stake , proof of space
    - proof of work just requires some computational challenge
    - proof of stake requires a token or an asset to "grease the gears"
    - proof of space requires each node to spend time building and providing a "pool" of "space"
permissioned and permissionless blockchains.
    - permissionless: open to anyone to participate in the network
    - permissioned: network limited only to designated participants, may not involve digital assets or tokens
data structures: hash chain, hash list, merkle tree

### Conclusions
- straw man blockchain
- blockchain as a ledger to help with storage implementation, similar to kafkasql but blockchainsql :)
- as something complementary to the sql mode, to add trust to sql storage, maybe using registry storage delegators we have