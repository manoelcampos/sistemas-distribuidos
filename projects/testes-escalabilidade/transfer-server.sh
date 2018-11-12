#!/usr/bin/env expect -f

#Transfere o pacote jar da aplicação de servidor de chat para uma máquina remota
#e em seguida executa tal aplicação.

set hostname "10.107.0.60" 
set username "aluno"
set password "aluno"

if { [lindex $argv 0] == "/?" || [lindex $argv 0] == "/h" || [lindex $argv 0] == "--h" } {
    puts "Uso: $argv0 \[IP\] \[username\] \[password\]"
    exit -1
}

if { [lindex $argv 0] != "" } {
    set hostname [lindex $argv 0]
}

if { [lindex $argv 1] != "" } {
    set username [lindex $argv 1]
}

if { [lindex $argv 2] != "" } {
    set password [lindex $argv 2]
}

proc enter_ssh_password {} {
    global password
    expect "assword:"
    send -- "$password\n"
    expect "*$ "
    #expect "*\]"
}

spawn scp -o "StrictHostKeyChecking no" target/scalability-tests-1.0.0.jar start-server.sh virtual-network-interfaces.sh $username@$hostname:/tmp/
enter_ssh_password

#BlockingChatServer NonBlockingChatServer
spawn ssh -o "StrictHostKeyChecking no" $username@$hostname "cd /tmp && ./start-server.sh NonBlockingChatServer"
enter_ssh_password
interact
#send -- "exit\n"