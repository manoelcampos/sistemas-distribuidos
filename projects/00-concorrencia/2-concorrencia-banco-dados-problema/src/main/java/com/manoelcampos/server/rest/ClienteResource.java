package com.manoelcampos.server.rest;

import com.manoelcampos.server.model.Cliente;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/cliente")
@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @GET
    public List<Cliente> all() {
        return Cliente.listAll();
    }
        
    @GET
    @Path("{id}")
    public Cliente findById(@PathParam("id") long id) {
        return Cliente.findById(id);
    }

    @POST
    public long insert(Cliente cliente) {
        Cliente.persist(cliente);
        return cliente.id;
    }
    
    @PUT
    public void update(Cliente cliente) {
        try{
            if(Cliente.update(cliente))
                return;
        }catch(Exception e){
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }

        //Se o objeto não for encontrado no BD, retorna código HTTP 404: página não encontrada.
        throw new WebApplicationException(Response.Status.NOT_FOUND);    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") long id) {
        Cliente cliente = Cliente.findById(id);

        if(cliente == null){
            //Se o objeto não for encontrado no BD, retorna código HTTP 404: página não encontrada.
            throw new WebApplicationException(Response.Status.NOT_FOUND);        
        }

        cliente.delete();
    }
}
