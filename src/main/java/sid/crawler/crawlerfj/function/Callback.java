package sid.crawler.crawlerfj.function;

import sid.crawler.crawlerfj.Page;

@FunctionalInterface
public interface Callback{
    void execute(Page page) throws Exception;
}