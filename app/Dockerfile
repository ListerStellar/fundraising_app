FROM gradle:jdk25-alpine AS build
WORKDIR /project
COPY build.gradle* settings.gradle* gradle.properties* ./
RUN gradle dependencies --no-daemon || true
COPY src ./src
RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine
RUN addgroup --system javauser && \
    adduser -S -s /bin/false -G javauser javauser
WORKDIR /app
COPY --from=build --chown=javauser:javauser /project/build/libs/app.jar app.jar
USER javauser
ENTRYPOINT ["java", "-jar", "app.jar"]