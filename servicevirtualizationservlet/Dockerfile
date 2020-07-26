FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/servicevirtualizationservlet-0.0.1.jar app.jar
COPY target/servicevirtualizationdata /usr/service-virtualization-data/servicevirtualizationdata
COPY target/classes/logback-spring.xml /usr/springbootlogging/logback-spring.xml
RUN mkdir /usr/springbootlogging/logs
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n", "-jar","/app.jar", "--debug"]
RUN /usr/sbin/addgroup appusergroup
RUN /usr/sbin/adduser -D -H -u 1001 -G appusergroup appuser
RUN chown -R appuser:appusergroup /usr/service-virtualization-data/servicevirtualizationdata
RUN chmod -R 755 /usr/service-virtualization-data/servicevirtualizationdata
RUN chown -R appuser:appusergroup /usr/springbootlogging
RUN chmod -R 777 /usr/springbootlogging
USER appuser