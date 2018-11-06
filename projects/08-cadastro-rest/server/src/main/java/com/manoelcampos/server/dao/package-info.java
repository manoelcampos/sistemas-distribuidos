/**
 * Contém as classes que são responsáveis por fazer
 * o acesso ao banco de dados, implementando o 
 * padrão de projetos 
 * <a href="https://pt.wikipedia.org/wiki/Objeto_de_acesso_a_dados">Data Access Objects (DAO)</a>. 
 * Tal padrão permite separar o código de acesso ao banco de dados das classes de negócio, 
 * tornando o projeto bastante organizado e fácil de manter. 
 * A implementação utiliza por padrão uma única classe genérica 
 * (recurso de Generics da linguagem Java) para evitar que tenhamos que criar uma 
 * classe DAO para cada classe de negócio que represente uma tabela do banco.
 * 
 * <p>Qualquer código de acesso ao banco é definido nestas classes,
 * como Criação (inclusão), Leitura, Alteração e Exclusão
 * de dados no banco, os chamados comandos CRUD (Create, Read, Update e Delete),
 * que se referem aos comandos SQL insert, select, update e delete.</p>
 * 
 * @author Manoel Campos da Silva Filho
 */
package com.manoelcampos.soapserver.dao;
