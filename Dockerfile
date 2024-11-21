FROM gradle:7.6-jdk17 AS builder
WORKDIR /app
COPY . .
RUN sed -i 's/\r$//' gradlew
RUN chmod +x ./gradlew
CMD ["./gradlew", "bootRun"]
