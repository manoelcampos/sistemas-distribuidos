var express = require('express')()
var http = require('http').Server(express)
var io = require('socket.io')(http)

express.get('/', function(req, res){
    res.sendFile(__dirname + '/index.html')
});

var porta = 3000
http.listen(porta, function(){
    console.log('Escutando na porta ' + porta)
});

io.on('connect', function(socket){
    console.log('\nCliente conectado: ' + socket.id)

    socket.on('disconnect', function(){
        console.log('Cliente desconectado: ' + socket.id)
    });
        
    socket.on('chat msg', function(msg){
        console.log('Mensagem: ' + msg)
        io.emit('chat msg', msg)
    });    

    socket.on('status', function(msg){
        console.log(msg)
        socket.broadcast.emit('status', msg)
    });
});

