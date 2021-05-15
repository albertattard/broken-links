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
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(final String[] args) throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        final String startLink = "https://albertattard.github.io/quickhacks/";

        final List<String> pending = new ArrayList<>();
        /* Start with an external link on purpose, so that I make sure that this is not followed.
           This will be removed later on. */
        // pending.add(startLink);
        pending.add("https://mvnrepository.com/");

        while (pending.isEmpty() == false) {

            final String pageLink = pending.remove(0);
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pageLink))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                System.out.printf("Broken link %s%n", pageLink);
                return;
            }

            /* We only need to check that links to other sites work, but we don't need to follow them.
               Thus we don't have to parse them. */
            if (pageLink.startsWith(startLink) == false) {
                System.out.printf("Will not follow link: %s%n", pageLink);
                continue;
            }

            System.out.println("Found the following links:");
            final Document document = Jsoup.parse(response.body());
            for (Element a : document.select("a[href~=http(s|)://.+]")) {
                System.out.printf("  > %s%n", a.attr("href"));
            }
        }
    }
}
