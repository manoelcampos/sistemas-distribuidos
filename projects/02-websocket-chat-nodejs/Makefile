linux:
	#Instala o nodejs e o gerenciador de pacotes (bibliotecas) npm
	sudo apt-get install nodejs npm -y
	#Usa o npm para instalar o nodemon globalmente (e não na pasta atual) para iniciar facilmente a app servidora
	sudo npm install -g nodemon
	#Adiciona o caminho das bibliotecas do node ao path do sistema, assim executáveis como o nodemon serão localizados ao serem digitados no terminal
	cat ~/.profile | grep 'PATH:~/node_modules/.bin' > /dev/null || echo 'export PATH=$$PATH:~/node_modules/.bin' >> ~/.profile
	# O comando ". arquivo_script" é o mesmo que "source arquivo_script",
	# mas funciona em diferentes shells.
	. ~/.profile
	
macos:
	brew install nodejs npm
	sudo npm install -g nodemon
	cat ~/.profile | grep 'PATH:~/node_modules/.bin' > /dev/null || echo 'export PATH=$$PATH:~/node_modules/.bin' >> ~/.profile
	# O comando ". arquivo_script" é o mesmo que "source arquivo_script",
	# mas funciona em diferentes shells.
	. ~/.profile

install:
	#Instala as dependências da aplicação servidora (as bibliotecas utilizadas)
	npm install

clean:
	rm -rf node_modules
