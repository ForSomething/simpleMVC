package Crawlerfj.Config.DefaultConfig;

import Crawlerfj.Common.StringUtil;
import Crawlerfj.Exception.ConfigIllegalException;
import Crawlerfj.Request.DefaultRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultConfigEntity {
    //对response进行操作的任务列表
    private List<CrawlingTask> taskList = null;

    //爬取的url
    private String url = null;

    //url的请求方式
    private DefaultRequest.Method method = null;

    //请求参数
    private Map<String,String> param = null;

    //自定义的请求头
    private Map<String,String> requestHeader = null;

    //对内容进行处理的function，这个主要是针对JSON内容的，例如用这个function抠出合法的JSON部分
    private ContentFormatter contentFormatter = null;

    public List<CrawlingTask> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<CrawlingTask> taskList) {
        this.taskList = taskList;
    }

    public void addTask(CrawlingTask entity){
        taskList.add(entity);
    }

    public DefaultConfigEntity(){
        taskList = new ArrayList<CrawlingTask>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DefaultRequest.Method getMethod() {
        return method;
    }

    public void setMethod(DefaultRequest.Method method) {
        this.method = method;
    }

    public Map<String,String> getParam() {
        return param;
    }

    public void setParam(Map<String,String> param) {
        this.param = param;
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public ContentFormatter getContentFormatter() {
        return contentFormatter;
    }

    public void setContentFormatter(ContentFormatter contentFormatter) {
        this.contentFormatter = contentFormatter;
    }

    //验证configEntity的合法性
    public boolean isLegal() throws ConfigIllegalException{
        //url、method、handleAction均不能为空
        if(StringUtil.IsNullOrWihtespace(url) || method == null)
            throw new ConfigIllegalException("the url is null or the method is null");
        return true;
    }

    @FunctionalInterface
    public interface ContentFormatter{
        String execute(String value);
    }
}