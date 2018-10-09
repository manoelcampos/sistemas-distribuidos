package com.manoelcampos.server.rest;

import com.manoelcampos.server.dao.DAO;
import com.manoelcampos.server.model.Usuario;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/usuario")
public class UsuarioResource {
    @Inject 
    private DAO<Usuario> dao;
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Usuario findById(@PathParam("id") long id) {
        return dao.findById(id);
    }

    //"cpf/{cpf : \\d{11}}"
    @GET
    @Path("cpf/{cpf : \\d{11}}")
    @Produces(MediaType.APPLICATION_JSON)
    public Usuario findByCpf(@PathParam("cpf") String cpf) {
        return dao.findByField("cpf", cpf);
    }
}
