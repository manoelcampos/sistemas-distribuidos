#!/bin/bash

#Inicia um servidor de chat em uma máquina remota 
#para onde o pacote jar da aplicação foi enviado.
#Este é um script auxiliar que é enviado para tal máquina remota pelo transfer-server.sh
#e executado localmente em tal máquina.
#Assim, ele não deve ser chamado manualmente.

if [[ $# -lt 2  ]]; then
    echo "Uso: $0 ChatServerClass IP"
    echo -e "\tChatServerClass - Nome da classe Java que representa o servidor de chat a iniciar"
    echo -e "\tIP - Informe o endereço IP no qual o servidor de chat vai ficar escutando"
    exit -1
fi

CHAT_SERVER_CLASS=$1
IP=$2

DEST_DIR=/tmp/scalability

rm -rf $DEST_DIR && \
mkdir -p $DEST_DIR && \
unzip scalability.jar -d $DEST_DIR && \
cd $DEST_DIR && pwd && \
java $CHAT_SERVER_CLASS $IP 