# Sử dụng JDK 23 làm base image
FROM openjdk:23-jdk-slim

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file JAR từ local vào container
COPY target/*.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
