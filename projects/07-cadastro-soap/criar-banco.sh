#!/bin/bash

SENHA_ROOT_MYSQL="root"

echo \
"drop database if exists cadastro_soap;
create database cadastro_soap;" | \
mysql -h localhost -uroot -p$SENHA_ROOT_MYSQL && echo "Banco criado com sucesso"