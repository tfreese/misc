javamagazin-netty-ws
=================

Das hier gezeigte Programm ist Bestandteil des Java Magazin Artikels 11.2012 "Netty - WebSockets to the rescue".

http://www.javamagazin.de/

Starten des Servers
=================
Der Server kann via mvn gestartet werden. Hierbei kann -DwsPort= und -DudpPort= verwendet werden um den 
jeweiligen Port zu spezifizieren.

\# mvn exec:exec -DwsPort=8888 -DudpPort=9999

Browser öffnen:
http://localhost:8888

Request simulieren:
echo 'Netty rockt' | nc -u localhost 9999