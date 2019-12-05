FROM openjdk:8-slim

EXPOSE 8080 9099 5005
ADD target/service.jar /opt/service/service.jar
WORKDIR /opt/service

ENTRYPOINT exec java \
            -jar \
            ${JAVA_OPTS} \
            service.jar
