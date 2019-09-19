#!/bin/bash
#

#BASEDIR=$PWD #Verzeichnis des Callers, aktuelles Verzeichnis
BASEDIR=$(dirname $0) #Verzeichnis des Skripts

PID_FILE=mavenProxy.pid

if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE);
fi

start()
{
    if [ -f $PID_FILE ]; then
		if [ "$(ps -e | grep -c $PID)" == "1" ]; then
			echo "MavenProxy already running with PID: $PID";
		else
			echo -n "Starting MavenProxy";
			
			java -classpath "$BASEDIR/misc-maven-proxy.jar:$BASEDIR/libs/*" de.freese.maven.proxy.MavenProxyApplication >> mavenProxy.log 2>&1 &
			
			echo $! > $PID_FILE && chmod 600 $PID_FILE;
			echo " with PID: $(cat $PID_FILE)";
		fi
    else
		echo -n "Starting MavenProxy";
		
		java -classpath "$BASEDIR/misc-maven-proxy.jar:$BASEDIR/libs/*" de.freese.maven.proxy.MavenProxyApplication >> mavenProxy.log 2>&1 &

		echo $! > $PID_FILE && chmod 600 $PID_FILE;
		echo " with PID: $(cat $PID_FILE)";
    fi    
}

stop() {
    if [ -f $PID_FILE ]; then
		echo "Stopping MavenProxy with PID: $PID";
		kill -15 $PID;
		rm $PID_FILE;
    else
		echo "Can not stop MavenProxy - no PID_FILE found!";
    fi

    #top -u ipps -n 1
}

status() {
    if [ -f $PID_FILE ] && [ "$(ps -e | grep -c $PID)" == "1" ]; then
         echo "MavenProxy already running with PID: $PID";
    else
         echo "MavenProxy is not running or PID_FILE not found";
    fi
}

#java -jar $BASEDIR/misc-maven-proxy-with-dependencies.jar de.freese.maven.proxy.MavenProxyApplication

case "$1" in
	start)
		start
		;;
	stop)
		stop
		;;
	restart)
		$0 stop
		$0 start
		;;
    status)
		status
        ;;
	*)
		echo "Usage: $0 {start|stop|restart|status}"
		;;
esac

