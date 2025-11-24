FROM openjdk:26-ea-21-slim AS builder

WORKDIR /app

COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests

FROM openjdk:26-ea-21-slim
WORKDIR /app

WORKDIR /app

COPY --from=builder /app/target/transporte-0.0.1-SNAPSHOT.jar /app/transporte.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "transporte.jar"]