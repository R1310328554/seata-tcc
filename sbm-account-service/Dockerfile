FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp

ARG MYSQL_IP=mysql-svc
ENV MYSQL_IP=$MYSQL_IP
ARG SEATA_IP=seata-server
ENV SEATA_IP=$SEATA_IP
ARG ZK_IP=zk-svc
ENV ZK_IP=$ZK_IP

RUN mkdir -p /app/bjc/
ADD target/sbm-account-service-1.1.0.jar /app.jar
#RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8020