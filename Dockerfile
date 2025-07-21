# Use OpenJDK image
FROM openjdk:21-jdk-slim

# Copy built JAR
ARG JAR_FILE=target/dubizlelike-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Run the application
ENTRYPOINT ["java", "-jar" , "/app.jar"]