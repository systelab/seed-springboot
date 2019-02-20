### STAGE 1: Build ###

# We label our stage as 'builder'
FROM maven:3.6.0-jdk-12-alpine as builder

## Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN mkdir /seed-springboot

WORKDIR /seed-springboot

COPY . .

## Build the angular app in production mode and store the artifacts in dist folder
RUN mvn package

### STAGE 2: Setup ###

FROM openjdk:12-jdk-oracle

COPY --from=builder /seed-springboot/target/seed-springboot-1.0.jar seed-springboot.jar


CMD ["java","-jar","seed-springboot.jar"]
