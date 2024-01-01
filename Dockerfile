FROM ubuntu:latest

RUN apt-get update && apt-get install -y openjdk-21-jre

WORKDIR /app

COPY ./server/library-1/ /app/library-1/
COPY ./server/library-2/ /app/library-2/
COPY ./server/library-3/ /app/library-3/

COPY ./server/build/server-1.0-jar-with-dependencies.jar /app/Server.jar
COPY .properties /app/.properties

EXPOSE 44000/udp

CMD ["java", "-jar", "Server.jar"]
