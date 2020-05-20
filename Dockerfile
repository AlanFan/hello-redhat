FROM registry.lab.gps-shanghai.com:5000/pfan/openjdk:8-jdk-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
