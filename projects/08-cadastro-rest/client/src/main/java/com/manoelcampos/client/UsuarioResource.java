package com.manoelcampos.client;

import java.io.Closeable;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Jersey REST client generated for REST resource:UsuarioResource [/usuario]<br>
 * USAGE:
 * <pre>
        UsuarioResource client = new UsuarioResource();
        Object response = client.XXX(...);
        // do whatever with response
        client.close();
   </pre>
 *
 * @author manoelcampos
 */
public class UsuarioResource implements Closeable {
    private final WebTarget webTarget;
    private final Client client;
    private static final String BASE_URI = "http://localhost:8080/server/api";

    public UsuarioResource() {
        client = ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("usuario");
    }

    public <T> T findByCpf(Class<T> responseType, String cpf) throws ClientErrorException {
        WebTarget resource = webTarget.path("cpf/" + cpf);
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T findById(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget.path(id);
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }

    @Override
    public void close() {
        client.close();
    }
    
    public static void main(String[] args) {
        try(UsuarioResource resource = new UsuarioResource()){
            Usuario usuario = resource.findById(Usuario.class, "1");
            System.out.println("ID: " + usuario.getId());
            System.out.println("Nome: " + usuario.getNome());
            System.out.println("CPF: " + usuario.getCpf());
            System.out.println();

            usuario = resource.findByCpf(Usuario.class, "22222222222");
            System.out.println("ID: " + usuario.getId());
            System.out.println("Nome: " + usuario.getNome());
            System.out.println("CPF: " + usuario.getCpf());        
        }
    }
}
