=========================================================
Aus sicherer Quelle - Dokumentation mit DocBook und Maven 
=========================================================

Autoren: 
Nico Alpert <nico.alpert@conceptpeople.de>
Bjarne Jansen <bjarne.jansen@conceptpeople.de>


Im folgenden wird beschrieben, wie das Maven-Beispielprojekt aufgesetzt
wird, dass die Dokumentation in drei verschiedenen Ausgabeformaten aus 
einer DocBook-Datei erzeugt. 


==================
Inhaltsverzeichnis
==================

	1. Installationsanleitung

	2. Dokumentation �ber die Konsole erzeugen

	3. Dokumentation �ber Eclipse erzeugen

	4. Dokumentation bearbeiten
	
	5. F�r Eilige

	6. F�r sehr Eilige


=========================
1. Installationsanleitung
=========================

F�r die Erzeugung der Dokumentation wird mindestens eine Java-Runtime 
und Apache Maven ben�tigt.

Ersteres sollte auf fast jedem Rechner bereits installiert sein und kann 
unter http://www.oracle.com/technetwork/java/javase/downloads/index.html
heruntergeladen werden.

Apache Maven steht unter http://maven.apache.org/download.html zum
Download bereit. Das Beispielprojekt sollte sowohl mit Maven 2 als 
auch mit Maven 3 funktionieren.

Sollte nach der Installation die Systemvariable MAVEN_HOME noch nicht 
gesetzt sein, muss diese hinzugef�gt werden. Die Variable zeigt auf den 
Installationsordner von Apache Maven.


==========================================
2. Dokumentation �ber die Konsole erzeugen
==========================================

Zun�chst muss die Zip-Datei "docbook-example.zip" entpackt werden. Als Ergebnis
sollte ein Ordner "docbook-example" mit zwei Unterordnern erzeugt werden.

Als n�chstes muss das docbook-styles Projekt gebaut werden. Dazu �ber die Konsole 
in den Ordner docbook-styles navigieren und den Befehl "mvn clean install" ausf�hren.
Nach der Ausf�hrung sollte BUILD SUCCESSFUL angezeigt werden.

Nun in den Ordner example-userguide wechseln und dort ebenfalls den Befehl
"mvn clean install" ausf�hren. Beim ersten Aufruf, kann die Ausf�hrung etwas dauern.
Auch hier wird zum Schluss BUILD SUCCESSFUL angezeigt.

Die Dokumentation wurde in drei verschiedenen Ausgabeformaten erzeugt. Diese befinden
sich unter "example-userguide\target\docbkx" sowie in der erzeugten zip-Datei
"example-userguide-1.0-SNAPSHOT.zip".
Die zugeh�rige Quelldatei "docbook_maven.xml" befindet sich unter 
"example-userguide\src\docbkx\". Um die �nderungen an der Quelldatei zu sehen, muss
der Befehl "mvn clean install" erneut in dem Ordner example-userguide ausgef�hrt werden.


======================================
3. Dokumentation �ber Eclipse erzeugen
======================================

Um die Erzeugung der Maven-Projekte zu vereinfachen, sollte Eclipse mit der 
Erweiterung "Maven Integration for Eclipse WTP" verwendet werden. Eclipse 
kann unter http://www.eclipse.org/downloads/ und das Plugin unter
http://marketplace.eclipse.org/content/maven-integration-eclipse-wtp
heruntergeladen werden.

Als n�chstes muss die Zip-Datei "docbook-example.zip" entpackt werden. Als Ergebnis
sollte ein Ordner docbook-example mit zwei Unterordnern zu sehen sein.

Anschlie�end muss Eclipse gestartet und der Ordner "docbook-example" als Workspace
ausw�hlt werden.

In Eclipse wird zun�chst "File" -> "Import..." und dann "Maven" -> "Existing Maven Projects"
ausw�hlt. Anschlie�en den Next-Button dr�cken.

In dem anschlie�enden Fenster ist als "Root Directory" der Ordner "docbook-example" auszuw�hlen.
Beide POMs der Beispielprojekte sollten jetzt angezeigt und auch ausgew�hlt werden. Die
Auswahl ist mit dem Finish-Button zu best�tigen.

Eclipse setzt die Projekte jetzt automatisch auf.

Anschlie�end auf dem "docbook-styles"-Projekt ein "Run As" -> "Maven install" ausf�hren. Das 
Projekt enth�lt die notwendigen Ressourcen f�r die Transformation der DocBook-Quelldatei in
die verschiedenen Ausgabeformate. Durch "Maven install" wird das Artefakt in den lokale M2-Ordner
abgelegt. Solange keine �nderungen an dem Projekt vorgenommen werden, ist ein erneutes Bauen 
nicht erforderlich.

Danach auf "example-userguide"-Projekt ebenfalls ein "Run As" -> "Maven install" ausf�hren. 
Als Ergebnis befindet sich unter example-userguide\target\ eine "example-userguide-1.0-SNAPSHOT.zip" mit
der Dokumentation in drei verschiedenen Ausgabeformaten. Unter example-userguide\target\docbkx ist die
Dokumentation auch ungezipt zu finden. Wann immer die Dokumentation bearbeitet wurde, ist ein erneutes
Ausf�hren von "Maven install" auf dem "example-userguide"-Projekt erforderlich.

Die Quelldatei docbook_maven.xml befindet sich unter "example-userguide\src\docbkx".

===========================
4. Dokumentation bearbeiten
===========================

Um die Dokumentation zu bearbeiten muss die docbook_maven.xml aus dem Ordner
"example-userguide\src\docbkx" angepasst werden. F�r die Bearbeitung sei der
WYSIWYG-Editor von xmlmind empfohlen. Dieser ist in der personal Version kostenlos
und nach kurzer Einarbeitung sehr gut f�r dies Aufgabe geeignet. Download unter:
http://www.xmlmind.com/xmleditor/download.shtml

Um neue Dokumente zu erzeugen kann in dem Editor "File" -> "New..." -> "DocBook v5+" -> "Book"
ausgew�hlt werden. Das neue Dokument muss in dem Ordner "example-userguide\src\docbkx" abgelegt
und in der pom des "example-userguide"-Projekts eingetragen werden.


=============
5. F�r Eilige
=============

- Java installieren (mindestens Runtime) 

- Apache Maven installieren (http://maven.apache.org/download.html)

- Eclipse mit dem Plugin "Maven Integration for Eclipse WTP" installieren
  (http://marketplace.eclipse.org/content/maven-integration-eclipse-wtp)

- "docbook-example.zip" entpacken

- Eclipse starten und den Ordner "docbook-example" als Workspace ausw�hlen.

- In Eclipse: "File" -> "Import..." Dann "Maven" -> "Existing Maven Projects" ausw�hlen und Next-Button dr�cken.

- Als "Root Directory" den Ordner "docbook-example" ausw�hlen (Beide POMs der Beispielprojekte
  sollten jetzt angezeigt und ausgew�hlt sein), Finish klicken.

- Eclipse setzt die Projekte jetzt automatisch auf.

- Anschlie�end auf dem "docbook-styles"-Projekt ein "Run As" -> "Maven install" ausf�hren 

- Danach auf "example-userguide"-Projekt ebenfalls ein "Run As" -> "Maven install" ausf�hren 

- Als Ergebnis befindet sich unter example-userguide\target\ eine "example-userguide-1.0-SNAPSHOT.zip" mit
  der Dokumentation in drei Ausgabeformaten. Unter example-userguide\target\docbkx ist die
  Dokumentation auch ungezipt zu finden.

- Die Quelldatei docbook_maven.xml befindet sich unter "example-userguide\src\docbkx".


==================
6. F�r sehr Eilige
==================

- Java installieren (mindestens Runtime) 

- Apache Maven installieren (http://maven.apache.org/download.html)

- "docbook-example.zip" entpacken

- In den Ordner "docbook-styles" navigieren und den Befehl "mvn clean install" ausf�hren.

- In den Ordner "example-userguide" navigieren und den Befehl "mvn clean install" ausf�hren.

- Als Ergebnis befindet sich unter example-userguide\target\ eine "example-userguide-1.0-SNAPSHOT.zip" mit
  der Dokumentation in drei Ausgabeformaten. Unter example-userguide\target\docbkx ist die
  Dokumentation auch ungezipt zu finden.

- Die Quelldatei docbook_maven.xml befindet sich unter "example-userguide\src\docbkx".
