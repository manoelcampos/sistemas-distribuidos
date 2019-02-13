package com.manoelcampos.server.rest;

import com.manoelcampos.server.dao.DAO;
import com.manoelcampos.server.model.Cliente;
import javax.inject.Inject;
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
import javax.ws.rs.core.MediaType;

@Path("/cliente")
@Transactional
public class ClienteResource {
    @Inject 
    private DAO<Cliente> dao;
        
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Cliente findById(@PathParam("id") long id) {
        /*Como a classe Cliente tem um atributo List<Endereco>
        e precisamos que tais endereços sejam carregados ao fazer o acesso
        ao serviço REST, a melhor prática é fazer um join para buscar
        tais endereços e evitar o erro LazyInitializationException.
        https://vladmihalcea.com/the-best-way-to-handle-the-lazyinitializationexception/.
        Observe que é usado um left join para, caso o cliente não tenh endereços,
        ele seja retornado assim mesmo.*/
        String jpql = "select c from Cliente c left join fetch c.enderecos where c.id = :id";
        TypedQuery<Cliente> query = dao.createQuery(jpql);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long insert(Cliente cliente) {
        return dao.save(cliente);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean update(Cliente cliente) {
        return dao.save(cliente) > 0;
    }

    @DELETE
    @Path("{id}")
    public boolean delete(@PathParam("id") long id) {
        return dao.delete(id);
    }
    
    
}
