FROM java:8-jre

VOLUME /tmp
ADD ./target/authorize-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]

EXPOSE 9000