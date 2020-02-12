package com.manoelcampos.server.rest;

import com.manoelcampos.server.dao.DAO;
import com.manoelcampos.server.model.Cliente;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status;

@Path("/cliente")
@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {
    /**
     * Devido ao uso da GraalVM para geração de aplicações nativas (que não existem
     * a JVM para executar), a recomendação do Quarkus é declarar atributos
     * injetados com visibilidade package.
     */
    @Inject 
    DAO<Cliente> dao;

    @GET
    public List<Cliente> all() {
        return dao.all();
    }
        
    @GET
    @Path("{id}")
    public Cliente findById(@PathParam("id") long id) {
        String jpql = "select c from Cliente c where c.id = :id";
        TypedQuery<Cliente> query = dao.createQuery(jpql);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @POST
    public long insert(Cliente cliente) {
        return dao.save(cliente);
    }
    
    @PUT
    public void update(Cliente cliente) {
        try {
            dao.save(cliente);
        }catch(OptimisticLockException e){
            Response response = Response.status(Status.CONFLICT)
                                        .entity("O cadastro foi alterado por outro usuário. Por favor, recarregue a página e tente novamente.")
                                        .type(MediaType.TEXT_PLAIN)
                                        .build();
            throw new WebApplicationException(e, response);
        }catch(Exception e){
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("{id}")
    public boolean delete(@PathParam("id") long id) {
        Cliente cliente = dao.findById(id);
        if(cliente == null){
            //Se o objeto não for encontrado no BD, retorna código HTTP 404: página não encontrada.
            throw new WebApplicationException(Response.Status.NOT_FOUND);        
        }
        
        return dao.delete(cliente);
    }
}
