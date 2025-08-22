name: maven

on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 1.8
      uses: actions/setup-java@v1
      with:
        java-version: 1.8
    - name: Build Blocking Socket
      run: mvn -B package --file 1.1-blocking-socket/pom.xml
    - name: Build Blocking SocketChannel
      run: mvn -B package --file 1.2-blocking-socketchannel/pom.xml
    - name: Build Non-Blocking SocketChannel
      run: mvn -B package --file 1.3-non-blocking-socketchannel/pom.xml
    - name: Build Parallel Non-Blocking SocketChannel
      run: mvn -B package --file 1.4-non-blocking-socketchannel-parallel/pom.xml
