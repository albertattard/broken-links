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
import java.util.Set;
import java.util.TreeSet;

public class Main {

    public static void main(final String[] args) throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        final String startLink = "https://albertattard.github.io/quickhacks/";

        final List<String> pending = new ArrayList<>();
        pending.add(startLink);

        final Set<String> visited = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        int testedLinks = 0;
        int brokenLinks = 0;

        while (pending.isEmpty() == false) {

            final String pageLink = pending.remove(0);

            /* Ignore links that were already visited */
            if (visited.add(pageLink) == false) {
                System.out.printf("Link already visited %s%n", pageLink);
                continue;
            }

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pageLink))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            testedLinks++;
            if (response.statusCode() >= 400) {
                brokenLinks++;
                System.out.printf("Broken link %s%n", pageLink);
                return;
            }

            /* We only need to check that links to other sites work, but we don't need to follow them.
               Thus we don't have to parse them. */
            if (pageLink.startsWith(startLink) == false) {
                System.out.printf("Will not follow link %s%n", pageLink);
                continue;
            }

            System.out.printf("Following link %s%n", pageLink);
            final Document document = Jsoup.parse(response.body());
            for (Element a : document.select("a[href~=http(s|)://.+]")) {
                final String link = a.attr("href");
                if (false == visited.contains(link) && false == pending.contains(link)) {
                    pending.add(link);
                }
            }
        }

        System.out.printf("Found %d broken links out of %d tested links%n", brokenLinks, testedLinks);
    }
}
