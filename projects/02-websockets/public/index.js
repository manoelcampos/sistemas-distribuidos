$(function () {
    const socket = io()

    socket.nickname = ''

    $('form').submit(() => submeterForm(socket))

    socket.on('chat msg', exibirMsg)
})

function exibirMsg(msg) {
    $('#messages').append($('<li>').text(msg))
}

function submeterForm(socket) {
    if (socket.nickname === '') {
        socket.nickname = $('#msg').val()
        socket.emit('login', socket.nickname)

        $('#msg').prop('placeholder', 'Digite uma mensagem');
        $('#button1').html('Enviar');

        socket.on('status', exibirMsgStatus)
        $('#msg').keypress(() => informaUsuariosInicioDigitacao(socket))

        $('#msg').keyup(() => socket.emit('status', ''))
    } else {
        socket.emit('chat msg', $('#msg').val())
    }

    $('#msg').val('')
    return false
}

function informaUsuariosInicioDigitacao(socket) {
    if (socket.nickname === '') {
        return
    }

    socket.emit('status', socket.nickname + ' está escrevendo...')
}

function exibirMsgStatus(msg) {
    $('#status').html(msg)
    console.log(msg)
}