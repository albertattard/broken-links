package quickhacks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.model.HttpRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {8888})
@DisplayName("Page download")
class PageDownloadTest {

    private final ClientAndServer client;

    public PageDownloadTest(final ClientAndServer client) {
        this.client = client;
    }

    @Test
    @DisplayName("should return client error when the server replies with a 404")
    void shouldReturnClientErrorWhenTheServerRepliesWithA404() throws IOException, InterruptedException {
        final HttpRequest request = request()
                .withMethod("GET")
                .withPath("/some-page-that-does-not-exists");
        client.when(request)
                .respond(response().withStatusCode(404));

        final PageDownload page = new PageDownload();
        final PageDownload.Result result = page.download("http://localhost:8888/some-page-that-does-not-exists");

        assertThat(result)
                .describedAs("The page download result should never be null")
                .isNotNull();

        assertThat(result.outcome())
                .describedAs("Page download should return ClientError when the given link resolved into a 404 error")
                .isEqualTo(PageDownload.Result.Outcome.CLIENT_ERROR);

        client.verify(request);
    }
}
