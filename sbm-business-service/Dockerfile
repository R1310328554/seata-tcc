FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
RUN mkdir -p /app/bjc/
ADD target/sbm-business-service-1.1.0.jar /app.jar
#RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8084