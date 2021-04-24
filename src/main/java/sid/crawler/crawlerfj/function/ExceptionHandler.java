package sid.crawler.crawlerfj.function;

import sid.crawler.crawlerfj.Page;

@FunctionalInterface
public interface ExceptionHandler {
    void execute(Exception exception, Page page, Callback callback);
}
