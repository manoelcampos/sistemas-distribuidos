package cadastro.soapserver.model;

import java.io.Serializable;


/**
 *
 * @author Manoel Campos da Silva Filho
 */
public interface Entidade extends Serializable {
    Long getId();
    void setId(Long id);
}
