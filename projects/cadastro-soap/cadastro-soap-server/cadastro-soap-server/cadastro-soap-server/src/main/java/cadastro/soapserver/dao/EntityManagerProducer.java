package cadastro.soapserver.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
@ApplicationScoped
public class EntityManagerProducer {
    @Produces
    @PersistenceUnit(unitName = "default")
    private EntityManagerFactory entityManagerFactory;
   
    @Produces
    @PersistenceContext(unitName = "default")
    private EntityManager em;
}
