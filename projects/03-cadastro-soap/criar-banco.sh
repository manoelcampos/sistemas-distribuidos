#!/bin/bash

SENHA_ROOT_MYSQL="root"
echo "create database if not exists cadastro_soap;"  | mysql -h localhost -uroot -p$SENHA_ROOT_MYSQL && echo "Banco criado com sucesso"