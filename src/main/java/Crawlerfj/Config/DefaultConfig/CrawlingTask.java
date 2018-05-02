package Crawlerfj.Config.DefaultConfig;

import Crawlerfj.Common.Const;
import Crawlerfj.Common.StringUtil;
import Crawlerfj.Exception.ConfigIllegalException;
import Crawlerfj.Request.DefaultRequest;

import java.util.List;
import java.util.Map;

public class CrawlingTask {
    //当前任务的类型
    private Const.taskType taskType = null;

    //任务列表，在当前任务是重定向时使用，指明重定向后要执行的任务
    private List<CrawlingTask> taskList = null;

    //自定义请求头，在当前任务是重定向时使用
    private Map<String,String> requestHeader = null;

    //自定义请求类型，在当前任务是重定向时使用
    private DefaultRequest.Method method = null;

    //自定义请求参数，在当前任务是重定向时使用
    private Map<String,String> param = null;

    //对内容进行处理的function，这个主要是针对JSON内容的，例如用这个function抠出合法的JSON部分
    private DefaultConfigEntity.ContentFormatter contentFormatter = null;

    //选择器,在getHtmlElement时使用，格式与jquery的选择器格式相同
    private String selector = null;

    //对爬取到的元素实行什么样的操作，在getHtmlElement时使用
    private Const.elementHandleAction elementHandleAction = null;

    //用以保存附件的文件夹的路径,在getHtmlElement时使用
    private String downloadFolderPath = null;

    //将构造函数访问限制设定为protected，不让在包外实例化此类
    protected CrawlingTask(){}

    public Const.taskType getTaskType() {
        return taskType;
    }

    protected void setTaskType(Const.taskType taskType) {
        this.taskType = taskType;
    }

    public String getSelector() {
        return selector;
    }

    protected void setSelector(String selector) {
        this.selector = selector;
    }

    public Const.elementHandleAction getElementHandleAction() {
        return elementHandleAction;
    }

    protected void setElementHandleAction(Const.elementHandleAction elementHandleAction) {
        this.elementHandleAction = elementHandleAction;
    }

    public String getDownloadFolderPath() {
        return downloadFolderPath;
    }

    protected void setDownloadFolderPath(String downloadFolderPath) {
        this.downloadFolderPath = downloadFolderPath;
    }

    public List<CrawlingTask> getTaskList() {
        return taskList;
    }

    protected void setTaskList(List<CrawlingTask> taskList){
        this.taskList = taskList;
    }

    public DefaultRequest.Method getMethod() {
        return method;
    }

    protected void setMethod(DefaultRequest.Method method) {
        this.method = method;
    }

    public Map<String, String> getParam() {
        return param;
    }

    protected void setParam(Map<String, String> param) {
        this.param = param;
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    protected void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public DefaultConfigEntity.ContentFormatter getContentFormatter() {
        return contentFormatter;
    }

    protected void setContentFormatter(DefaultConfigEntity.ContentFormatter contentFormatter) {
        this.contentFormatter = contentFormatter;
    }

    //验证每个TaskEntity的合法性
    private boolean isLegal() throws ConfigIllegalException {
        if(StringUtil.IsNullOrWihtespace(selector) || taskType == null){
            throw new ConfigIllegalException("the selector in ElementEntity is null or the handleAction in ElementEntity is null");
        }
        return true;
    }
}
