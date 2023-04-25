FROM maven:3.9.1-amazoncorretto-17 as build
WORKDIR /app
COPY . .
RUN mvn verify package
FROM amazoncorretto:17
COPY --from=build /app/target/Blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/lib/Blockchain.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/Blockchain.jar"]