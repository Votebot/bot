FROM gradle AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :shard:clean :shard:jar --no-daemon

FROM adoptopenjdk:8-jre-openj9
RUN mkdir /app
COPY --from=build /home/gradle/src/shard/build/libs/*.jar /app/shard.jar
CMD ["java", "-jar", "/app/shard.jar"]