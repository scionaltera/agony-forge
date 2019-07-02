FROM openjdk:8-jdk-alpine
MAINTAINER Peter Keeler <scion@agonyengine.com>
EXPOSE 8080
COPY agony-forge-demo-*.jar /opt/mud/app.jar
WORKDIR /opt/mud
CMD ["/usr/bin/java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "app.jar"]
