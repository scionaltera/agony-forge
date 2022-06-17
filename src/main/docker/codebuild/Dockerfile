FROM amazoncorretto:8-alpine-jdk as build
MAINTAINER Peter Keeler <scion@agonyforge.com>
WORKDIR /opt/build
COPY . /opt/build/
RUN cd /opt/build \
&& apk update \
&& apk add --no-cache bash \
&& ./gradlew --console=plain clean build -x docker -x dependencyCheckAnalyze

FROM amazoncorretto:8-alpine-jre
MAINTAINER Peter Keeler <scion@agonyforge.com>
EXPOSE 8080
COPY --from=build /opt/build/build/libs/agony-forge-*.jar /opt/mud/app.jar
CMD ["/usr/bin/java", "-jar", "/opt/mud/app.jar"]
