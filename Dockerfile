FROM adoptopenjdk/openjdk16

WORKDIR /usr/app

COPY build/install .

ENTRYPOINT ["bin/votebot"]
