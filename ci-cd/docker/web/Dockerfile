#Dockerfile pour lancer GeolocIndoorWeb

FROM node:lts-stretch-slim

RUN mkdir /app

WORKDIR /app

COPY /GeolocIndoorWeb/ /app/

RUN npm cache clean --force

WORKDIR /app/GeolocIndoorWeb

RUN npm install

EXPOSE 4200

ENTRYPOINT ["npm", "run", "dc"]