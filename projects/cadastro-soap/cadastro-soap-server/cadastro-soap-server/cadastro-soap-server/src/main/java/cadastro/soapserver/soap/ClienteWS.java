package cadastro.soapserver.soap;

import cadastro.soapserver.dao.DAO;
import cadastro.soapserver.model.Cliente;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
@WebService(serviceName = "ClienteWS")
@Stateless
public class ClienteWS {

    @WebMethod(operationName = "adicionar")
    public boolean adicionar(@WebParam(name = "cliente") Cliente cliente) {
        DAO<Cliente> dao = new DAO<>(Cliente.class);
        return dao.persist(cliente);
    }
    
    @WebMethod(operationName = "getById")
    public Cliente getById(@WebParam(name = "id") Long id) {
        DAO<Cliente> dao = new DAO<>(Cliente.class);
        return dao.getById(id);
    }    
}
