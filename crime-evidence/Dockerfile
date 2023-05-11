FROM amazoncorretto:17-alpine
RUN mkdir -p /opt/laa-crime-evidence/
WORKDIR /opt/laa-crime-evidence/
COPY ./build/libs/crime-evidence.jar /opt/laa-crime-evidence/app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8189 8199
CMD java -jar app.jar
