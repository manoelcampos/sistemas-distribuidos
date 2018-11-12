#!/bin/bash

#Execute este script no computador servidor de chat para criar várias interfaces de rede virtuais.
#Isso aumenta o número de conexões que o servidor consegue aceitar.
#Os números IPs gerados aqui devem ser passados por linha de comando para o app Java AppSocketChatScalability.
#Assim, ele vai criar clientes que conectarão em tais IPs.

if [[ $# -lt 4 ]]; then
    echo "Uso: $0 NomeInterfaceRede EnderecoRede EnderecoHost TotalInterfacesRede"
    echo -e "\tNomeInterfaceRede Nome da interface física de rede para criar interfaces virtuais"
    echo -e "\tOctetoInicial   Último octeto do endereço IP a ser atribuído à primeira interface virtual."
    echo -e "\t                (considerando apenas máscara /24)."
    echo -e "\tEnderecoRede 3 primeiros octetos do endereço IP da rede atual (considerando apenas máscara /24)."
    echo -e "\t             Um exemplo seria 10.104.0 se o IP do servidor for 10.104.0.8"
    echo -e "\tTotalInterfacesRede número de interfaces virtuais a serem criadas"
    exit -1
fi

NomeInterfaceRede="$1"
EnderecoRede="$2"
OctetoInicial="$3"
TotalInterfacesRede="$4"

for i in $(seq 1 $TotalInterfacesRede) 
do
   (( octeto = OctetoInicial + i ))
   echo "Criando interface virtual $NomeInterfaceRede:$octeto com IP $EnderecoRede.$octeto"
   sudo ifconfig $NomeInterfaceRede:$octeto $EnderecoRede.$octeto/24
done