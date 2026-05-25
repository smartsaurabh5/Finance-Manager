FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /workspace
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw -q dependency:go-offline
COPY src src
RUN ./mvnw -q clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
RUN addgroup --system finance && adduser --system --ingroup finance finance
COPY --from=build /workspace/target/manager-0.0.1-SNAPSHOT.jar app.jar
USER finance

EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
