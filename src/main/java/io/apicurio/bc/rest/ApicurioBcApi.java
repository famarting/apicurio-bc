package io.apicurio.bc.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.apicurio.bc.BcBlock;

@Path("/api/bc")
public interface ApicurioBcApi {

    @Path("/blocks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlockchain();

    @Path("/blocks")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public BcBlock generateNextBlock(String data);

    @Path("/peers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getPeers();

    @Path("/peers")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void addPeer(String peer);

}
