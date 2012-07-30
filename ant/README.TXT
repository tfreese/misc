AUEL EDV Beratung GmbH
Thomas Freese
18.03.2005

CVS-Zugriff:

Hier ist zu beachten das ANT ein externes CVS-Programm nutzt. Dazu muss
CVS in der <PATH> Variablem des jeweiligen Betriebssystems enthalten sein.
Für Windows ist im IPPS_ANT Projekt eine entsprechende cvs.exe enthalten.
Unter Linux sollte der CVS-Client eigentlich zu jeder Distribution dazugehören.

Ebenso greift ANT auf die cvspass-Datei des aktuellen Users zu (~/.cvspass).
Diese Datei kann in der Konsole wie folgt erstellt werden:
	cvs -d :pserver:<USER>@<SERVER>:<REPOSITORY> login
Passwort eingeben und fertig.
Alternativ wird diese Datei von ANT automatisch angelegt, wenn sie noch nicht vorhanden ist.
Hierfür muss dann die build.properties entsprechen angepast werden.

Auszug aus der ANT-Docu:
This task needs "cvs" on the path. If it isn't, you will get an error
(such as error 2 on windows). If <cvs> doesn't work, try to execute cvs.exe
from the command line in the target directory in which you are working. 

Desweiteren muss die Jar-Datei jsch*.jar im Classpath der ANT-Runtime
vorhanden sein.
