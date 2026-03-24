FROM eclipse-temurin:8-jdk-jammy AS builder

WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:8-jre-jammy

WORKDIR /usr/local
RUN apt-get update \
    && apt-get install --no-install-recommends -y nmap \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace/target/*.jar /usr/local/ctoip.jar

EXPOSE 8081
CMD ["java", "-jar", "/usr/local/ctoip.jar"]
