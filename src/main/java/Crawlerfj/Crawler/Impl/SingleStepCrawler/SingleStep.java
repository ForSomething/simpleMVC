package Crawlerfj.Crawler.Impl.SingleStepCrawler;

import Util.HttpUtil.ResponseEntity;

@FunctionalInterface
public interface SingleStep {
    void Execute (ResponseEntity responseEntity, SingleStepConfig singleStepConfig);
}
