# Use a Java base image
FROM ubuntu:latest

# Install JRE 21
RUN apt-get update && apt-get install -y openjdk-21-jre

# Set the working directory inside the container
WORKDIR /app

COPY ./server/library-1/ /app/library-1/
COPY ./server/library-2/ /app/library-2/
COPY ./server/library-3/ /app/library-3/

# Copy the server application JAR file to the container
COPY ./server/build/server-1.0-jar-with-dependencies.jar /app/Server.jar
COPY .properties /app/.properties

# Expose the port on which the server will listen
EXPOSE 44111/udp
EXPOSE 44222/udp
EXPOSE 44333/udp



# Set the command to run the server application
CMD ["java", "-jar", "Server.jar"]
