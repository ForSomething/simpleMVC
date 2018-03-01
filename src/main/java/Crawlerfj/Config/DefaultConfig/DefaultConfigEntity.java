package Crawlerfj.Config.DefaultConfig;

import Crawlerfj.Common.Const;
import Crawlerfj.Common.StringUtil;
import Crawlerfj.Exception.ConfigIllegalException;
import Crawlerfj.Request.DefaultRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class DefaultConfigEntity {
    //对response进行操作的任务列表
    private ArrayList<TaskEntity> taskList = null;

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

    public ArrayList<TaskEntity> getTaskList() {
        return taskList;
    }

    public void addTask(TaskEntity entity){
        taskList.add(entity);
    }

    public DefaultConfigEntity(){
        taskList = new ArrayList<TaskEntity>();
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

    public static class TaskEntity{
        //当前任务的类型
        private Const.taskType taskType = null;

        //任务列表，在当前任务是重定向时使用，指明重定向后要执行的任务
        private ArrayList<TaskEntity> taskList = null;

        //选择器,在getHtmlElement时使用，格式与jquery的选择器格式相同
        private String selector = null;

        //对爬取到的元素实行什么样的操作，在getHtmlElement时使用
        private Const.elementHandleAction elementHandleAction = null;

        //用以保存附件的文件夹的路径,在getHtmlElement时使用
        private String downloadFolderPath = null;

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

        public ArrayList<TaskEntity> getTaskList() {
            return taskList;
        }

        protected void setTaskList(ArrayList<TaskEntity> taskList){
            this.taskList = taskList;
        }

        //验证每个TaskEntity的合法性
        private boolean isLegal() throws ConfigIllegalException{
            if(StringUtil.IsNullOrWihtespace(selector) || taskType == null){
                throw new ConfigIllegalException("the selector in ElementEntity is null or the handleAction in ElementEntity is null");
            }
            return true;
        }
    }

    @FunctionalInterface
    public interface ContentFormatter{
        String execute(String value);
    }
}
