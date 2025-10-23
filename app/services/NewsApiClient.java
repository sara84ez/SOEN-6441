package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import models.Source;
import play.cache.AsyncCacheApi;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class NewsApiClient {

    private static final Logger.ALogger log = Logger.of(NewsApiClient.class);

    private final WSClient ws;
    private final String baseUrl;
    private final Optional<String> apiKey;
    private final AsyncCacheApi cache;
    private final Duration cacheTtl;

    @Inject
    public NewsApiClient(WSClient ws, Config config, AsyncCacheApi cache) {
        this.ws = ws;
        this.cache = cache;
        this.baseUrl = config.hasPath("newsapi.baseUrl") ? config.getString("newsapi.baseUrl") : "https://newsapi.org/v2";
        this.apiKey = config.hasPath("newsapi.key") ? Optional.ofNullable(config.getString("newsapi.key")) : Optional.empty();
        int ttlSeconds = config.hasPath("newsapi.cacheTtlSeconds") ? config.getInt("newsapi.cacheTtlSeconds") : 300;
        this.cacheTtl = Duration.ofSeconds(ttlSeconds);
    }

    public CompletionStage<List<Source>> getSources(Optional<String> country, Optional<String> category, Optional<String> language) {
        String cacheKey = "sources:" + country.orElse("") + ":" + category.orElse("") + ":" + language.orElse("");
        return cache.getOrElseUpdate(cacheKey, () -> fetchSources(country, category, language), cacheTtl);
    }

    private CompletionStage<List<Source>> fetchSources(Optional<String> country, Optional<String> category, Optional<String> language) {
        if (apiKey.isEmpty() || apiKey.get().isBlank()) {
            log.warn("NEWSAPI_KEY not configured. Returning empty list.");
            return CompletableFuture.completedFuture(List.of());
        }

        WSRequest req = ws.url(baseUrl + "/sources").addQueryParameter("apiKey", apiKey.get());
        country.filter(s -> !s.isBlank()).ifPresent(v -> req.addQueryParameter("country", v));
        category.filter(s -> !s.isBlank()).ifPresent(v -> req.addQueryParameter("category", v));
        language.filter(s -> !s.isBlank()).ifPresent(v -> req.addQueryParameter("language", v));

        return req.get().thenApply(WSResponse::asJson).thenApply(json -> {
            List<Source> list = new ArrayList<>();
            JsonNode sources = json.get("sources");
            if (sources != null && sources.isArray()) {
                for (JsonNode s : sources) {
                    Source src = new Source(
                        text(s, "id"),
                        text(s, "name"),
                        text(s, "description"),
                        text(s, "url"),
                        text(s, "category"),
                        text(s, "language"),
                        text(s, "country")
                    );
                    list.add(src);
                }
            } else {
                log.warn("Unexpected response from NewsAPI: " + json);
            }
            return list;
        });
    }

    private static String text(JsonNode node, String field) {
        JsonNode n = node.get(field);
        return (n == null || n.isNull()) ? "" : n.asText();
    }
}
