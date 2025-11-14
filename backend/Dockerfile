# Use the official OpenJDK image as the base image
FROM amazoncorretto:25-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to the container
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .

# Copy the Maven wrapper directory
COPY .mvn .mvn

# Copy the source code
COPY src ./src

# Build the application
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Expose the port the app runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/imageorganiser-0.0.1-SNAPSHOT.jar"]