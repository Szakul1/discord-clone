#FROM maven:3.6.0-jdk-11-slim AS MAVEN_BUILD
#
#COPY pom.xml /build/
#COPY src /build/src
#
#WORKDIR /build
#RUN mvn package
#
#FROM openjdk:11
#
#WORKDIR /app
#
#COPY --from=MAVEN_BUILD /build/target/discord-0.0.1-SNAPSHOT.jar /app
#
#ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "discord-0.0.1-SNAPSHOT.jar"]

FROM openjdk:11
COPY ./target/discord-0.0.1-SNAPSHOT.jar discord-0.0.1-SNAPSHOT.jar
CMD ["java", "-Dspring.profiles.active=dev", "-jar", "discord-0.0.1-SNAPSHOT.jar"]

#FROM maven:3.8.2-jdk-8
#
#WORKDIR /app
#COPY . .
#RUN mvn clean install
#
#CMD mvn spring-boot:run