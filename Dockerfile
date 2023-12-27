# Use a Java base image
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

RUN mkdir /app/library
COPY ./server/library/music.wav /app/library/music.wav

# Copy the server application JAR file to the container
COPY ./server/server-fortesting.jar /app/Server.jar

# Expose the port on which the server will listen
EXPOSE 4445/udp

# Set the command to run the server application
CMD ["java", "-jar", "Server.jar"]
