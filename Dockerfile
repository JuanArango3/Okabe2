FROM eclipse-temurin:17-jdk-alpine as mvn-deps
WORKDIR /opt/app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw --no-transfer-progress dependency:go-offline

FROM mvn-deps as builder
COPY ./src ./src
RUN ./mvnw --no-transfer-progress clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.jar /opt/app/okabe2.jar
ENV BOT_TOKEN default
ENTRYPOINT ["java", "-jar", "/opt/app/okabe2.jar" ]