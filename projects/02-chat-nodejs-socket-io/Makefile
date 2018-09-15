all:
	sudo apt-get install nodejs npm -y
	sudo npm install -g nodemon
	cat ~/.profile | grep 'PATH:~/node_modules/.bin' > /dev/null || echo 'export PATH=$$PATH:~/node_modules/.bin' >> ~/.profile
	source ~/.profile
clean:
	rm -rf node_modules
