# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY target/smatech-store-service-0.0.1-SNAPSHOT.jar app.jar


# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

