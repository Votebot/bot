FROM gradle AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :shardManager:clean :shardManager:jar --no-daemon

FROM adoptopenjdk:8-jre-openj9
RUN mkdir /app
COPY --from=build /home/gradle/src/shardManager/build/libs/*.jar /app/shardmanager.jar
CMD ["java", "-jar", "/app/shardmanager.jar"]