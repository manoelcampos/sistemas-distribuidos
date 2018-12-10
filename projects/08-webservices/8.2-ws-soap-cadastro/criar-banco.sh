#!/bin/bash

SENHA_ROOT_MYSQL="root"

echo \
"drop database if exists cadastro_soap;
create database cadastro_soap charset=utf8;

use cadastro_soap;

CREATE TABLE Estado (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  estado varchar(255) NOT NULL,
  uf varchar(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE Cidade (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  nome varchar(255) NOT NULL,
  estado_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_estado (estado_id),
  CONSTRAINT fk_cidade_estado FOREIGN KEY (estado_id) REFERENCES Estado (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE Marca (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  nome varchar(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE Produto (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  descricao varchar(255) NOT NULL,
  marca_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_marca (marca_id),
  CONSTRAINT fk_produto_marca FOREIGN KEY (marca_id) REFERENCES Marca (id)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO Estado  VALUES (1,'Tocantins','TO');
INSERT INTO Cidade  VALUES (1,'Palmas',1),(2,'Porto Nacional',1);
INSERT INTO Marca   VALUES (1,'Samsung'),(2,'LG'),(3,'Brastemp');
INSERT INTO Produto VALUES (1,'TV 4',1),(2,'Lavadora de Roupas Brastemp 15KG',3);
" | \
mysql -h localhost -uroot -p$SENHA_ROOT_MYSQL && echo "Banco criado com sucesso"