#!/usr/bin/env expect -f

#Transfere o pacote jar da aplicação de servidor de chat para uma máquina remota
#e em seguida executa tal aplicação.

set hostname "10.104.0.103" 
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


#NonBlockingChatServer BlockingChatServer AppSocketChatScalability
spawn scp -o "StrictHostKeyChecking no" target/scalability-tests-1.0.0.jar $username@$hostname:/tmp/scalability.jar
spawn scp -o "StrictHostKeyChecking no" start-server.sh $username@$hostname:/tmp

spawn ssh -o "StrictHostKeyChecking no" $username@$hostname "sh /tmp/start-server.sh NonBlockingChatServer $hostname"
expect "assword:"
send -- "$password\n"
expect "*$ "
interact
#send -- "exit\n"