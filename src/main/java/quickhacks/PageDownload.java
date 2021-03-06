package quickhacks;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PageDownload {

    private final HttpClient client = createHttpClient();

    public Result download(final String link) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .timeout(Duration.ofSeconds(5))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        return new Result(Result.Outcome.CLIENT_ERROR);
    }

    public static record Result(Outcome outcome) {
        enum Outcome {
            CLIENT_ERROR
        }
    }

    private static HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }
}
