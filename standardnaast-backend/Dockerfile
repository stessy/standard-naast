# Stage 1: Build application
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy root pom and backend pom
COPY pom.xml ./pom.xml
COPY standardnaast-backend/pom.xml ./standardnaast-backend/pom.xml
COPY standardnaast-backend/src ./standardnaast-backend/src

# Build backend module
RUN mvn -pl standardnaast-backend clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy jar from build stage
COPY --from=builder /app/standardnaast-backend/target/standardnaast-backend-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
