package crawlerfj.function;

import crawlerfj.Page;

@FunctionalInterface
public interface Callback{
    void execute(Page page) throws Exception;
}