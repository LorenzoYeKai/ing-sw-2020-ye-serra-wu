# Polimi Software Engineering Project 2020 - Santorini
## GC50 - Ye - Serra - Wu

[![Java CI with Maven](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/actions) 

[Check Live Server Status](http://18.195.117.7:8000)

#### [!!!Descrizione da completare!!!]

### Features
- [X] Complete Game Rules (Basic gods) 
- [X] CLI
- [X] GUI (partial implementation)
- [X] Socket
- [X] Advanced features #1: Undo
- [X] Advanced features #2: Lobby and multiple matches in parallel 

### Build and test the Game Server:
```
mvn package -P BuildServer
```
You can also download [the latest version of server](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/releases/download/tip/server-progetto-ingegneria-del-software-serra-ye-wu-1.0-SNAPSHOT.jar) directly from the [Releases](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/releases) page.


### Run the Game Server:
```
java -jar server-progetto-ingegneria-del-software-serra-ye-wu-1.0-SNAPSHOT.jar
```
The Game Server use TCP Port 12345, make sure this port is not being used and it's opened in your firewall.

### Live server
We have an AWS EC2 Server which automatically runs the latest version of server on 18.195.117.7:12345.

You can monitor and manage the live server at http://18.195.117.7:8000

### Build and test the Game Client: 
```
mvn package -P BuildClient
```
You can also download [the latest version of client](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/releases/download/tip/client-progetto-ingegneria-del-software-serra-ye-wu-1.0-SNAPSHOT.jar) directly from the [Releases](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/releases) page.

### Play the game by running the client:
```
java -jar client-progetto-ingegneria-del-software-serra-ye-wu-1.0-SNAPSHOT.jar
```
If you want to connect to our live server, just write `18.195.117.7` when the game client asks for the server IP address.

### You can launch the GUI with this command:
```
java -jar client-progetto-ingegneria-del-software-serra-ye-wu-1.0-SNAPSHOT.jar gui
```

