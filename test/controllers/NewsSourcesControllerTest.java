package controllers;

import models.Source;
import org.junit.Test;
import org.mockito.Mockito;
import play.mvc.Result;
import services.NewsApiClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class NewsSourcesControllerTest {

    @Test
    public void apiReturnsJson() throws Exception {
        NewsApiClient fake = Mockito.mock(NewsApiClient.class);
        when(fake.getSources(any(), any(), any())).thenReturn(
            CompletableFuture.completedFuture(List.of(
                new Source("x","X","desc","https://x.test","general","en","us")
            ))
        );
        NewsSourcesController c = new NewsSourcesController(fake);
        Result r = c.api("us", "", "en").toCompletableFuture().get();
        assertThat(r.status()).isEqualTo(200);
        assertThat(play.test.Helpers.contentType(r)).isEqualTo("application/json");
    }
}
