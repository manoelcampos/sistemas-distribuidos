linux:
	(expect -v && echo "Comando expect já instalado") || \
	((sudo apt-get install expect -y 2> /dev/null || \
	 brew install expect 2> /dev/null || \
	 echo "Não foi possível instalar o comando expect, utilizada pelos scripts de automatização da execução das aplicações para teste de escalabilidade" >&2 && false) && \
	 echo "Comando expect instalado com sucesso")
