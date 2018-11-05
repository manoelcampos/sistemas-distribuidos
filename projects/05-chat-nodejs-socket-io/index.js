/**
 * Servidor de Chat utilizando a biblioteca 
 * socket.io (https://socket.io)
 * com NodeJS.
 * 
 * A biblioteca express (https://expressjs.com)
 * é utilizada para criar um servidor web que vai aguardar
 * requisições HTTP.
 */

var express = require('express')()
var http = require('http').Server(express)
var io = require('socket.io')(http)

//Porta na qual o servidor vai ficar aguardando requisições do WebSocket.
var porta = 8000

/**
 * Função que faz o servidor ficar escutando a porta do WebSocket,
 * aguardando requisições.
 * Quando a função é iniciada, a aplicação começa a escutar tal porta.
 */
http.listen(porta, function(){
    console.log('Servidor iniciado. Abra o navegador em http://localhost:' + porta)
});

/**
 * Função que vai responder à requisições à página inicial do servidor (raíz do site),
 * normalmente http://localhost:porta/
 * A porta é definida acima.
 * 
 * Quando uma requisição à raíz for solicitada,
 * é retornado o conteúdo do arquivo index.html para o cliente.
 * Assim, ele poderá ver a interface para interagir no chat.
 */
express.get('/', function (req, res) {
    res.sendFile(__dirname + '/index.html')
});

/**
 * Função executada quando um cliente conecta no servidor.
 * Isto ocorre quando o cliente abre a página
 * http://localhost:porta/
 * 
 * Cada aba que o cliente abrir no navegador para tal endereço
 * vai representar um novo usuário conectado ao servidor.
 * Assim, a cada aba ou janela aberta em tal endereço,
 * esta função é chamada para iniciar a conexão do cliente com o servidor.
 */
io.on('connect', function(socket){
    console.log('\nCliente conectado: ' + socket.id)

    /*
    Função chamada quando um cliente desconecta do servidor 
    (fechando a aba ou janela do navegador ou perdendo a conexão).
    */
    socket.on('disconnect', function(){
        console.log('Cliente desconectado: ' + socket.id)
    });
        
    /*
    Função chamada quando uma mensagem do tipo "chat msg" é enviada por um cliente.
    Tal mensagem representa um conversa do cliente.
    O tipo "chat msg" foi definido por nós. 
    A biblioteca socket.io nos permite definir qualquer
    tipo de mensagem que desejarmos.
    O tipo da mensagem permite criar funções específicas no servidor
    que tratam somente aquele tipo de mensagem.
    Assim, o código fica bem organizado.
    Não precisamos incluir if's para verificar qual o tipo
    da mensagem recebida, uma vez que a função abaixo
    vai ser chamada somente quando mensagens do tipo especificado
    forem enviadas.
    */
    socket.on('chat msg', function(msg){
        console.log('Mensagem: ' + msg)
        io.emit('chat msg', msg)
    });    

    /*
    Função chamada quando um cliente envia uma mensagem de status
    para o servidor. Tal mensagem pode indicar, por exemplo, que
    o usuário está digitando.
    Quando uma mensagem deste tipo é recebida, é feito broadcast
    dela, ou seja, ela é enviada para todos os outros usuários.
    Assim, eles saberão o status de um determinado usuário.
     */
    socket.on('status', function(msg){
        console.log(msg)
        socket.broadcast.emit('status', msg)
    });
});

