# Stage 1: Build app
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Stage 2: Run app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Install tzdata for timezone configuration
RUN apk add --no-cache tzdata
ENV TZ=Asia/Ho_Chi_Minh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 9999

# Optimization for Render Free Tier (512MB RAM)
# -Xmx320m: Max heap size (conservative to leave room for native memory)
# -Xms128m: Lower initial heap
# -XX:MaxMetaspaceSize=96m: Limit metaspace
# -XX:ReservedCodeCacheSize=64m: Limit code cache
# -XX:+UseSerialGC: Use Serial GC for lowest overhead
# -XX:+ExitOnOutOfMemoryError: Exit immediately if OOM occurs
CMD ["java", "-Xmx320m", "-Xms128m", "-XX:MaxMetaspaceSize=96m", "-XX:ReservedCodeCacheSize=64m", "-XX:+UseSerialGC", "-XX:+ExitOnOutOfMemoryError", "-Dserver.port=${PORT:-9999}", "-jar", "app.jar"]
