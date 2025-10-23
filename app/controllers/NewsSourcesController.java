package controllers;

import models.Source;
import play.libs.Json;
import play.mvc.*;
import services.NewsApiClient;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class NewsSourcesController extends Controller {

    private final NewsApiClient client;

    @Inject
    public NewsSourcesController(NewsApiClient client) {
        this.client = client;
    }

    public CompletionStage<Result> list(String country, String category, String language, String format) {
        Optional<String> c = Optional.ofNullable(country).filter(s -> !s.isBlank());
        Optional<String> cat = Optional.ofNullable(category).filter(s -> !s.isBlank());
        Optional<String> lang = Optional.ofNullable(language).filter(s -> !s.isBlank());

        return client.getSources(c, cat, lang).thenApply(sources -> {
            if ("json".equalsIgnoreCase(format)) {
                return ok(Json.toJson(sources)).as("application/json");
            }
            return ok(views.html.sources.render(sources, country == null ? "" : country,
                    category == null ? "" : category, language == null ? "" : language));
        });
    }

    public CompletionStage<Result> api(String country, String category, String language) {
        Optional<String> c = Optional.ofNullable(country).filter(s -> !s.isBlank());
        Optional<String> cat = Optional.ofNullable(category).filter(s -> !s.isBlank());
        Optional<String> lang = Optional.ofNullable(language).filter(s -> !s.isBlank());

        return client.getSources(c, cat, lang).thenApply(sources -> ok(Json.toJson(sources)).as("application/json"));
    }
}
