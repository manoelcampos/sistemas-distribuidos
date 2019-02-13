package com.manoelcampos.server.rest;

import com.manoelcampos.server.dao.DAO;
import com.manoelcampos.server.model.Usuario;
import javax.inject.Inject;
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

@Path("/usuario")
@Transactional
public class UsuarioResource {
    @Inject 
    private DAO<Usuario> dao;
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Usuario findById(@PathParam("id") long id) {
        return dao.findById(id);
    }

    @GET
    @Path("cpf/{cpf : \\d{11}}")
    @Produces(MediaType.APPLICATION_JSON)
    public Usuario findByCpf(@PathParam("cpf") String cpf) {
        return dao.findByField("cpf", cpf);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long insert(Usuario usuario) {
        return dao.save(usuario);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean update(Usuario usuario) {
        return dao.save(usuario) > 0;
    }

    @DELETE
    @Path("{id}")
    public boolean delete(@PathParam("id") long id) {
        return dao.delete(id);
    }
    
    
}
