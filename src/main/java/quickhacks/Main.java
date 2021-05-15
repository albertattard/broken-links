package quickhacks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Main {

    public static void main(final String[] args) throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        final String pageLink = "https://albertattard.github.io/quickhacks/";
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(pageLink))
                .timeout(Duration.ofSeconds(5))
                .build();

        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Found the following links:");
        final Document document = Jsoup.parse(response.body());
        for (Element a : document.select("a[href]")) {
            System.out.printf("  > %s%n", a.attr("href"));
        }
    }
}
