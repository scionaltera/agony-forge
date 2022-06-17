FROM amazoncorretto:8-alpine-jdk
MAINTAINER Peter Keeler <scion@agonyforge.com>
EXPOSE 8080
COPY agony-forge-*.jar /opt/mud/app.jar
WORKDIR /opt/mud
CMD ["/usr/bin/java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "app.jar"]
