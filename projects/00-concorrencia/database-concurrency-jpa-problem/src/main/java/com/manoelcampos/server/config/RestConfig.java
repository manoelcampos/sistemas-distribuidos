package com.manoelcampos.server.config;

import javax.ws.rs.core.Application;

/**
 * Com o Quarkus esta arquiva não é necessária.
 * Se não for incluída, os endpoints REST
 * deverão ser acessados a partir da raiz do site.
 * 
 * @author manoelcampos
 */
@javax.ws.rs.ApplicationPath("api")
public class RestConfig extends Application {


    
}
