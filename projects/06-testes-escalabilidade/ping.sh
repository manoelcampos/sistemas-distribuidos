#!/bin/bash

#Envia um ping para os computadores de uma sub-rede até encontrar um que esteja ativo.

rede="10.104.0."
for i in {3..255}
do
   echo "Pingando em $rede$i"
   ping -c 2 -t 2 $rede$i > /dev/null && echo "Máquina $rede$i disponível" && exit 1
done