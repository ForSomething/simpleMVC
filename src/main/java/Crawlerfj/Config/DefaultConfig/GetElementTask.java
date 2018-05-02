package Crawlerfj.Config.DefaultConfig;

import Crawlerfj.Common.Const;

public class GetElementTask {
    private CrawlingTask task;

    public GetElementTask(){
        task = new CrawlingTask();
        task.setTaskType(Const.taskType.getHtmlElement);
    }

    public void setSelector(String selector){
        task.setSelector(selector);
    }
}
