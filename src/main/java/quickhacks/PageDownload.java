package quickhacks;

import java.io.IOException;

public class PageDownload {

    public Result download(final String link) throws IOException, InterruptedException {
        return null;
    }

    public static record Result(Outcome outcome) {
        enum Outcome {
            CLIENT_ERROR
        }
    }
}
