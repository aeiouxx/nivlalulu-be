FROM gradle:8.11.0-jdk21 AS builder
WORKDIR /builder
COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle src ./src

RUN gradle bootJar

RUN java -Djarmode=layertools -jar build/libs/*.jar extract --destination extracted


FROM bellsoft/liberica-openjre-debian:21.0.5-cds

WORKDIR /application
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

EXPOSE 8443
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
