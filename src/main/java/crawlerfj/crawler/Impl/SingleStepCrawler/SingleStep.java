package crawlerfj.crawler.Impl.SingleStepCrawler;

import toolroom.httputil.ResponseEntity;

@FunctionalInterface
public interface SingleStep {
    void Execute (ResponseEntity responseEntity, SingleStepConfig singleStepConfig);
}
