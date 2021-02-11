#Dockerfile pour lancer GeoLocIndoorApi

FROM gradle:6.1-jdk11

RUN apt-get update -y
RUN apt-get install -y sqlite sqlite3 spatialite-bin libsqlite3-mod-spatialite

WORKDIR /app

COPY GeoLocIndoorApi GeoLocIndoorApi
COPY GeoLocIndoorCommon GeoLocIndoorCommon

WORKDIR /app/GeoLocIndoorApi

#RUN ./gradlew bootJar
RUN gradle bootJar

ENTRYPOINT ["java", "-Dspring.config.location=src/main/resources/application-dev.yml","-jar","/app/GeoLocIndoorApi/build/libs/GeoLocIndoorApi.jar"]
