# Use Eclipse Temurin (Java 17) as base image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src src

# Install Maven and build the application
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Expose the application port
EXPOSE 8080

# Set the entrypoint to run the JAR
ENTRYPOINT ["java", "-jar", "target/jobseeker-0.0.1-SNAPSHOT.jar"]
