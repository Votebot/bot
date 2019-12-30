FROM adoptopenjdk:8-jre-openj9
RUN mkdir /app
COPY shard/build/libs/*.jar /app/shard.jar
CMD ["java", "-jar", "/app/shard.jar"]