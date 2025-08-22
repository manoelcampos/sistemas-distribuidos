#!/bin/bash

clear
echo "Envia requisição para WebService SOAP dos Correios para consulta de endereço pelo CEP"
echo "A requisição é enviada por meio do protocolo HTTP usando a ferramenta de linha de comando curl"
echo -e "Pode-se passar um CEP que deseja consultar por parâmetro\n"

cep="77.021-090"
if [[ $# -gt 0 ]]; then
   cep="$1"
fi

curl https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente \
     --data \
     "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:cli='http://cliente.bean.master.sigep.bsb.correios.com.br/'> 
        <soapenv:Body>
          <cli:consultaCEP>
            <cep>$cep</cep>
          </cli:consultaCEP>
        </soapenv:Body>
      </soapenv:Envelope>"

echo -e "\n\n"
