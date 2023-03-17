FROM openjdk:17-jdk-slim
RUN mkdir -p /opt/laa-crime-evidence/
WORKDIR /opt/laa-crime-evidence/
COPY ./build/libs/crime-evidence.jar /opt/laa-crime-evidence/app.jar
RUN useradd -r -u 1001 appuser
USER 1001
EXPOSE 8189 8199 8091
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8091
ENTRYPOINT ["java","-jar","app.jar"]
