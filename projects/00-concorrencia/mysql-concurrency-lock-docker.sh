#!/bin/bash

# Este script faz o mesmo que o mysql-concurrency-lock.sh,
# mas automaticamente cria um container com um servidor MySQL instalado e configurado.
# Assim, você não precisa se preocupar em instalar o MySQL,
# mas precisa ter o Docker instalado e executando.

clear

# Finaliza o script se algum erro ocorrer
set -e

docker -v || echo "Você precisa instalar e iniciar o Docker para executar este script."

# Nome do container Docker onde o servidor MySQL será instalado (não precisa alterar)
CONTAINER_NAME="mysql-container"

# Senha a ser atribuída ao usuário root do servidor MySQL a ser instalado em um container docker (não precisa alterar)
SENHA_ROOT='rootroot'

echo "Mostra como é possível usar o comando 'select ... for update' do MySQL para criar um lock"
echo "e assim impedir que outro usuário (em outra transação) consiga fazer alterações"
echo "em um registro enquanto as alterações do primeiro usuário não forem confirmadas com o comando commit"
echo ""
echo "Para testar este exemplo, abra dois terminais e execute o script em cada um."
echo "Cada terminal representa um usuário acessando o banco."
echo "Você verá que o segundo fica parado à espera do update no primeiro ser confirmado (commit)."
echo ""

# Cria um arquivo SQL na pasta /tmp, contendo os comandos a serem executados no banco
echo "
create database if not exists concorrencia;
use concorrencia;

# Cria a tabela se ela não existir e insere um registro nela.
create table if not exists cidade as select 1 as id, CONVERT('Palmas', CHAR(512)) as nome;
select '# Tentando selecionar dados (pode demorar por conta de lock em outra operação concorrente)' as '';

# Locks só funcionam dentro de transações
start transaction;

# Seleciona o registro da cidade 1, criando um lock para evitar o acesso
# enquanto a transação não for finalizada.
# Como vamos alterar o registro logo abaixo, o lock impede o acesso ao registro 
# neste meio tempo.
select * from cidade where id = 1 for update;

\! echo ''
\! echo '# Alterando registro e aguardando alguns segundos para conseguirmos perceber o lock.'
\! echo '# Neste intervalo, execute o script em outro terminal'
\! echo '# e verá que ele não consegue nem sequer obter os dados do registro usando o primeiro select.'
\! echo ''

# Altera o nome da cidade para um valor aleatório.
# Aguarda 10 segundos de propósito, para simular uma demora na conclusão do update.
# Assim, você consegue ver que o segundo terminal fica parado esperando este update finalizar.
update cidade set nome = concat('Aleatório ', rand()) where id = 1 and sleep(10) >= 0;

# Confirma as alterações e libera o lock.
# Se foi o script foi aberto em outro terminal,
# a partir deste momento a outra instância do script terá acesso
# ao registro que estava bloqueado.
commit;

# Exibe os dados depois da confirmação das alterações acima.
# Se colocarmos um sleep antes deste select, poderemos inclusive
# ver alterações feitas por outros usuários (outras instâncias deste script).
select * from cidade;
" > /tmp/script.sql

if docker inspect --format '{{json .State.Running}}' "$CONTAINER_NAME" 1>/dev/null 2>/dev/null; then
    echo "Container com o servidor MySQL já está rodando."
else
    echo "Iniciando container com o servidor MySQL."
    docker run --name "$CONTAINER_NAME" -e MYSQL_ROOT_PASSWORD=$SENHA_ROOT -d mysql  
fi

# Copia o script SQL para o container com o servidor MySQL
docker cp /tmp/script.sql "$CONTAINER_NAME":/tmp/script.sql

# Executa os comandos do arquivo SQL criado acima
docker exec "$CONTAINER_NAME" sh -c "mysql -u root -p$SENHA_ROOT < /tmp/script.sql"
#docker exec "$CONTAINER_NAME" mysql -u root -p$SENHA_ROOT < /tmp/script.sql
#docker exec -it "$CONTAINER_NAME" mysql -u root -p$SENHA_ROOT
#docker exec -it "$CONTAINER_NAME" bash
