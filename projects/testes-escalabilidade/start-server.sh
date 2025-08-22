#!/bin/bash

#Inicia um servidor de chat em uma máquina remota 
#para onde o pacote jar da aplicação foi enviado.
#Este é um script auxiliar que é enviado para tal máquina remota pelo transfer-server.sh
#e executado localmente em tal máquina.
#Assim, ele não deve ser chamado manualmente.

if [[ $# -lt 1 ]]; then
    echo "Uso: $0 ChatServerClass"
    echo -e "\tChatServerClass - Nome da classe Java que representa o servidor de chat a iniciar"
    exit -1
fi

CHAT_SERVER_CLASS=$1

DEST_DIR=/tmp/scalability

rm -rf $DEST_DIR && \
mkdir -p $DEST_DIR && \
unzip scalability-tests-1.0.0.jar -d $DEST_DIR && \
cd $DEST_DIR && pwd && \
java $CHAT_SERVER_CLASS 