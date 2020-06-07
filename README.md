# Progetto di Ingegneria di Informatica 2020 - Santorini
## Gruppo 50, Ye - Serra - Wu

[![Java CI with Maven](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/actions) 

[Check Live Server Status](http://18.195.117.7:8000)

[Descrizione da completare]

### Compilare e testare il jar del server:
```
mvn package -P BuildServer
```
Si può anche scaricare direttamente [la versione più recente del server](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/releases/download/tip/server-progetto-ingegneria-del-software-serra-ye-wu-1.0-SNAPSHOT.jar) dalla pagina [Releases](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/releases).


### Eseguire il server:
```
java -jar server-progetto-ingegneria-del-software-serra-ye-wu-1.0-SNAPSHOT.jar
```
Il nostro server utilizza la porta TCP 12345.

### Live server
Abbiamo un Server EC2 di AWS che ospita automaticamente la versione più recente del server sul 18.195.117.7:12345.

Si può monitorare e gestire il live server accendo al http://18.195.117.7:8000

### Compilare e testare il client: 
```
mvn package -P BuildClient
```
Si può anche scaricare direttamente [la versione più recente del client](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/releases/download/tip/client-progetto-ingegneria-del-software-serra-ye-wu-1.0-SNAPSHOT.jar) dalla pagina [Releases](https://github.com/Kishin98/ing-sw-2020-ye-serra-wu/releases).

### Eseguire il client:
```
java -jar client-progetto-ingegneria-del-software-serra-ye-wu-1.0-SNAPSHOT.jar
```
Per connettere al live server, basta scrivere `18.195.117.7` come l'indirizzo IP del server.
