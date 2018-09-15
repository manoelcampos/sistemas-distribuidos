package cadastro.soapserver.soap;

import cadastro.soapserver.dao.DAO;
import cadastro.soapserver.model.Produto;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Manoel Campos da Silva Filho
 * 
 * @see http://setgetweb.com/p/WAS85x/ae/jaxws.html
 * @see https://blogs.oracle.com/arungupta/totd-124:-using-cdi-jpa-with-jax-rs-and-jax-ws
 * @see https://blogs.oracle.com/arungupta/totd-123:-f:ajax,-bean-validation-for-jsf,-cdi-for-jsf-and-jpa-20-criteria-api-all-in-one-java-ee-6-sample-application
 */
@WebService(serviceName = "ProdutoWS")
@Stateless
public class ProdutoWS {
    @Inject 
    private DAO<Produto> dao;

    @WebMethod(operationName = "adicionar")
    public boolean adicionar(@WebParam(name = "produto") Produto produto) {
        return dao.persist(produto);
    }
    
    @WebMethod(operationName = "getById")
    public Produto getById(@WebParam(name = "id") long id) {
        return dao.getById(id);
    }    
}
