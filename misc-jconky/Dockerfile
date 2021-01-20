# docker build -t <IMAGE_NAME> .;
# docker build --build-arg JAR_FILE=<JAR in target Folder> -t <IMAGE_NAME>:<VERSION> .;
# docker build --build-arg JAR_FILE=<JAR in target Folder> -t <IMAGE_NAME>:<VERSION> -f <Dockerfile>;
# docker run [-d] --cpus="2" --name <CONTAINER_NAME> -e VM_PARAMS="-Xms64m -Xmx128m" -p 8081:8081 <IMAGE_NAME>:<VERSION> --spring.profiles.active=test;
# docker start/stop <CONTAINER_NAME>;

# docker build -t jconky .
# docker run -d --name jconky -e VM_PARAMS="-Xms16m -Xmx64m -DLOG_DIR=/jconky/logs" -v /home/tommy/.java/jconky:/jconky/logs jconky:latest
# docker start/stop jconky;
#FROM openjdk:15-alpine
FROM openjdk:latest
#FROM ubuntu:latest
 
MAINTAINER Thomas Freese <commercial@freese-home.de>
 
ARG JAR_FILE=shade/misc-jconky.jar
ENV VM_PARAMS="-Xms12m -Xmx64m"

COPY ${JAR_FILE} /jconky/jconky.jar

RUN echo "Europe/Berlin" > /etc/timezone

# GUI-Bibiotheken für JavaFX installieren -> Kennt den Befehl 'apt-get' nicht
#RUN apt-get update && apt-get install --no-install-recommends -y xorg libgl1-mesa-glx && rm -rf /var/lib/apt/lists/*
##RUN apt-get update && apt-get install --no-install-recommends -y xvfb openjfx && rm -rf /var/lib/apt/lists/*
###RUN apt-get update && apt-get install libgtk-3-0 libglu1-mesa -y && apt-get update

WORKDIR /
VOLUME /tmp

ENTRYPOINT exec java \
 $VM_PARAMS \
 -XX:TieredStopAtLevel=1 \
 -Djava.security.egd=file:/dev/./urandom \
 -jar \
 /jconky/jconky.jar \
 $0 $@


