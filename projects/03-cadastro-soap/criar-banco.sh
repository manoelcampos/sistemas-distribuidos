#!/bin/bash

SENHA_ROOT_MYSQL="root"

echo \
"create database if not exists cadastro_soap;
use cadastro_soap;
create table if not exists marca(id int not null auto_increment primary key, nome varchar(255) not null);
create table if not exists produto(id int not null auto_increment primary key, descricao varchar(255) not null, marca_id int, foreign key (marca_id) references marca(id));
insert into marca(nome) values('Samsung');
insert into produto(descricao, marca_id) values ('TV', 1);" | \
mysql -h localhost -uroot -p$SENHA_ROOT_MYSQL && echo "Banco criado com sucesso"