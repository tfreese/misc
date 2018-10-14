#!/bin/bash
#
# Thomas Freese
echo jSync

#BASEDIR=$PWD #Verzeichnis des Callers, aktuelles Verzeichnis
BASEDIR=$(dirname $0) #Verzeichnis des Skripts
cd $BASEDIR

if [ ! -f target/classes/de/freese/jsync/JSyncConsole.class ]; then
    mvn compile
fi

mvn -q exec:java -Dexec.mainClass="de.freese.jsync.JSyncConsole" -Dexec.args="$1 $2 $3 $4 $5 $6" -Dexec.classpathScope=runtime
#mvn -q exec:exec -Dexec.executable="target/classes/de/freese/jsync/JSyncConsole.class" -Dexec.args="$@" -Dexec.classpathScope=runtime
# exec:exec execute programs and Java programs in a separate process.
# exec:java execute Java programs in the same VM.
    
cd ~
