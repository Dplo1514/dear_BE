FROM openjdk:8u111-jre-alpine
ARG JAR_FILE=build/libs/*.jar
COPY $JAR_FILE app.jar
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","-Dspring.profiles.active=${active}","app.jar"]