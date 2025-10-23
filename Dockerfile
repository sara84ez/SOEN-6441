# --- Build stage
FROM hseeberger/scala-sbt:17.0.9_1.9.9_2.13.12 as builder
WORKDIR /app
COPY . .
RUN sbt -Dsbt.color=false -Dsbt.log.noformat=true clean test jacoco:cover stage

# --- Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /opt/app
ENV PLAY_SECRET="please-change-me"     NEWSAPI_KEY=""
COPY --from=builder /app/target/universal/stage/ ./
EXPOSE 9000
CMD ["bash", "-lc", "bin/p3-news-sources -Dplay.http.secret.key=$PLAY_SECRET -Dhttp.port=9000"]
