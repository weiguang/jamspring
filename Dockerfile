FROM eclipse-temurin:21-jdk-alpine

# service path
ENV SERVICE_PATH=/app/service
ENV SERVICE_VERSION=1.0.0

WORKDIR ${SERVICE_PATH}

RUN mkdir -p ${SERVICE_PATH}/
RUN mkdir -p ${SERVICE_PATH}/config
RUN mkdir -p ${SERVICE_PATH}/src/build

COPY ./okayjam-web-core/target/okayjam-web-core-${SERVICE_VERSION}-SNAPSHOT.jar ${SERVICE_PATH}/

COPY ./config ${SERVICE_PATH}/config/
COPY ./service.sh ${SERVICE_PATH}/
RUN chmod a+x ${SERVICE_PATH}/service.sh

# net tool
RUN apk add --no-cache procps iputils

COPY ./docker-entrypoint.sh /usr/local/bin
RUN chmod a+x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT [ "docker-entrypoint.sh" ]

# same as server port
EXPOSE 8080