FROM mirrors.tencent.com/tjdk/tencentkona21-ts4:21.0.9

# service path
ENV SERVICE_PATH=/app/service

WORKDIR ${SERVICE_PATH}

RUN mkdir -p ${SERVICE_PATH}/
RUN mkdir -p ${SERVICE_PATH}/config
RUN mkdir -p ${SERVICE_PATH}/src/build
# password
RUN yum install -y shadow-utils
RUN PASSWORD=$(dd bs=1 count=12 if=/dev/urandom | md5sum | cut -d' ' -f1) && echo "root:$PASSWORD" | chpasswd -c SHA512
#RUN PASSWORD="123abcde" && echo "root:$PASSWORD" | chpasswd -c SHA512


COPY ./rms-workOrder-core/target/rms-workOrder-core-1.0-SNAPSHOT.jar ${SERVICE_PATH}/

COPY ./config ${SERVICE_PATH}/config/
COPY ./service.sh ${SERVICE_PATH}/


# net tool
RUN yum install procps-ng -y
RUN yum install iputils -y

COPY ./docker-entrypoint.sh /usr/local/bin
RUN chmod a+x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT [ "docker-entrypoint.sh" ]

# same as server port
EXPOSE 8080