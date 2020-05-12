FROM maven:3.6.0-jdk-8-alpine

RUN apk add openjdk8
RUN apk add jq
RUN apk add -Uuv python less py-pip openssl tzdata
RUN pip install awscli

RUN apk --purge -v del py-pip && \
    rm /var/cache/apk/*

WORKDIR /build

COPY pom.xml .
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.1:go-offline

COPY src/ /build/src
COPY bin/run.sh .

RUN mvn install --offline

ENTRYPOINT ["./run.sh"]
