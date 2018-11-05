package com.manoelcampos.correios;

import br.com.correios.bsb.sigep.master.bean.cliente.AtendeCliente;
import br.com.correios.bsb.sigep.master.bean.cliente.AtendeClienteService;
import br.com.correios.bsb.sigep.master.bean.cliente.EnderecoERP;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class BuscaCep {
    public static void main(String[] args) {
        try { 
            final AtendeClienteService service = new AtendeClienteService();
            final AtendeCliente port = service.getAtendeClientePort();
            final String cep = "77022348";

            final EnderecoERP result = port.consultaCEP(cep);
            System.out.println("Endereço: " + result.getEnd());
            System.out.println("Bairro: " + result.getBairro());
            System.out.println("Cidade: " + result.getCidade());
            System.out.println("UF: " + result.getUf());
        } catch (Exception ex) {
            System.out.println("Erro ao consultar CEP: " + ex.getMessage());
        }
    }
}
