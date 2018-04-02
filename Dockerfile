### STAGE 1: Build ###

# We label our stage as 'builder'
FROM maven:alpine as builder

## Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN mkdir /seed-springboot

WORKDIR /seed-springboot

COPY . .

## Build the angular app in production mode and store the artifacts in dist folder
RUN mvn package

### STAGE 2: Setup ###

FROM openjdk:8-jre-alpine


COPY --from=builder /seed-springboot/target/springboot-test-1.0.jar seed-springboot.jar


CMD ["java","-jar","seed-springboot.jar"]
