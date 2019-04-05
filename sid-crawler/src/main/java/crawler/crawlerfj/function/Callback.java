package crawler.crawlerfj.function;

import crawler.crawlerfj.Page;

@FunctionalInterface
public interface Callback{
    void execute(Page page) throws Exception;
}