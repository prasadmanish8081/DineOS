FROM maven:3.9.4-eclipse-temurin-20 as build
WORKDIR /app
COPY backend/pom.xml backend/mvnw backend/mvnw.cmd ./backend/
COPY backend/.mvn ./backend/.mvn
COPY backend/src ./backend/src
WORKDIR /app/backend
RUN chmod +x mvnw
RUN ./mvnw -DskipTests package -B

FROM eclipse-temurin:20-jre
WORKDIR /app
COPY --from=build /app/backend/target/dineos-backend-0.0.1-SNAPSHOT.jar ./app.jar
ENV JAVA_OPTS=""
ENV PORT 8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
