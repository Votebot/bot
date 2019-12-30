FROM adoptopenjdk:8-jre-openj9
RUN mkdir /app
COPY shardManager/build/libs/*.jar /app/shardmanager.jar
CMD ["java", "-jar", "/app/shardmanager.jar"]