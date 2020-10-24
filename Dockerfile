FROM gradle AS builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle installDist -Dorg.gradle.daemon=false

FROM adoptopenjdk:openj9
WORKDIR /opt/VoteBot
COPY --from=builder /home/gradle/src/build/install/VoteBot /opt/VoteBot/
CMD ["/opt/VoteBot/bin/VoteBot", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication"]