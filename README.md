# P3: News Sources (Play Framework + Java)

Minimal Play (Java) service for listing **NewsAPI** sources with optional filters: **country**, **category**, **language**.
- HTML page at `/sources`
- JSON API at `/api/sources`

## Docker (recommended)

```bash
docker build -t p3-news-sources .

docker run --rm -p 9000:9000   -e NEWSAPI_KEY=YOUR_NEWSAPI_KEY   -e PLAY_SECRET=some-long-random-secret   p3-news-sources

# open:
# http://localhost:9000/sources?country=ca&language=en
```

## Local development

- Java 17+, sbt 1.9+  
- `sbt run` – start server on 9000  
- `sbt test` – run tests  
- `sbt jacoco` – coverage report

## Config

- Set `NEWSAPI_KEY` via environment variable at runtime.
- Optional: tweak `newsapi.cacheTtlSeconds` in `conf/application.conf`.

## Routes

```
GET /sources      controllers.NewsSourcesController.list(country, category, language, format="html")
GET /api/sources  controllers.NewsSourcesController.api(country, category, language)
```

## Notes

- Non-blocking HTTP via `WSClient`
- Simple caching with `AsyncCacheApi` (Ehcache)
