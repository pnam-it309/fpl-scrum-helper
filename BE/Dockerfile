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
# -Xmx420m: Max heap size
# -Xms200m: Lower initial heap to save RAM
# -XX:+UseSerialGC: Use Serial GC for lower overhead
# -Dserver.port=${PORT:-9999}: Bind to Render's $PORT
CMD ["java", "-Xmx420m", "-Xms200m", "-XX:+UseSerialGC", "-Dserver.port=${PORT:-9999}", "-jar", "app.jar"]
