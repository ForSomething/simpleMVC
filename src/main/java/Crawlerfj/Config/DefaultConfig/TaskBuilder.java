package Crawlerfj.Config.DefaultConfig;

import Crawlerfj.Common.Const;
import Crawlerfj.Request.DefaultRequest;

import java.util.ArrayList;
import java.util.Map;

public class TaskBuilder {
    public static GetHtmlElementTaskBuilder CreateGetHtmlElementTaskBuilder(){
        return new GetHtmlElementTaskBuilder();
    }

    public static GetContentTaskBuilder CreateGetContentTaskBuilder(){
        return new GetContentTaskBuilder();
    }

    public static DoRedirectTaskBuilder CreateDoRedirectTaskBuilder(){
        return new DoRedirectTaskBuilder();
    }

    public static class GetHtmlElementTaskBuilder{
        private String selector = null;
        private Const.elementHandleAction elementHandleAction = null;
        private String downloadFolderPath = null;

        //私有化构造函数，只有调用TaskBuilder的CreateGetHtmlElementTask方法才能实例化此类对象
        private GetHtmlElementTaskBuilder(){}

        public String getDownloadFolderPath() {
            return downloadFolderPath;
        }

        public void setDownloadFolderPath(String downloadFolderPath) {
            this.downloadFolderPath = downloadFolderPath;
        }

        public Const.elementHandleAction getElementHandleAction() {
            return elementHandleAction;
        }

        public void setElementHandleAction(Const.elementHandleAction elementHandleAction) {
            this.elementHandleAction = elementHandleAction;
        }

        public String getSelector() {
            return selector;
        }

        public void setSelector(String selector) {
            this.selector = selector;
        }

        public DefaultConfigEntity.TaskEntity CreateTaskEntity(){
            DefaultConfigEntity.TaskEntity entity = new DefaultConfigEntity.TaskEntity();
            entity.setDownloadFolderPath(this.getDownloadFolderPath());
            entity.setElementHandleAction(this.getElementHandleAction());
            entity.setSelector(this.getSelector());
            entity.setTaskType(Const.taskType.getHtmlElement);
            return entity;
        }
    }

    public static class GetContentTaskBuilder{
        //私有化构造函数，只有调用TaskBuilder的CreateGetContentTask方法才能实例化此类对象
        private GetContentTaskBuilder(){}

        public DefaultConfigEntity.TaskEntity CreateTaskEntity(){
            DefaultConfigEntity.TaskEntity entity = new DefaultConfigEntity.TaskEntity();
            entity.setTaskType(Const.taskType.getContent);
            return entity;
        }
    }

    public static class DoRedirectTaskBuilder{
        private String selector = null;
        private ArrayList<DefaultConfigEntity.TaskEntity> taskList;
        private Map<String,String> requestHeader = null;
        private DefaultRequest.Method method = null;
        private Map<String,String> param = null;
        private DefaultConfigEntity.ContentFormatter contentFormatter = null;
        //私有化构造函数，只有调用TaskBuilder的CreateDoRedirectTask方法才能实例化此类对象
        private DoRedirectTaskBuilder(){taskList = new ArrayList<DefaultConfigEntity.TaskEntity>();}

        public Map<String, String> getRequestHeader() {
            return requestHeader;
        }

        public void setRequestHeader(Map<String, String> requestHeader) {
            this.requestHeader = requestHeader;
        }

        public Map<String, String> getParam() {
            return param;
        }

        public void setParam(Map<String, String> param) {
            this.param = param;
        }

        public DefaultRequest.Method getMethod() {
            return method;
        }

        public void setMethod(DefaultRequest.Method method) {
            this.method = method;
        }

        public String getSelector() {
            return selector;
        }

        public void setSelector(String selector) {
            this.selector = selector;
        }

        public void AddTask(DefaultConfigEntity.TaskEntity entity){
            taskList.add(entity);
        }

        public void setTaskList(ArrayList<DefaultConfigEntity.TaskEntity> taskList) {
            this.taskList = taskList;
        }

        public DefaultConfigEntity.ContentFormatter getContentFormatter() {
            return contentFormatter;
        }

        public void setContentFormatter(DefaultConfigEntity.ContentFormatter contentFormatter) {
            this.contentFormatter = contentFormatter;
        }

        public DefaultConfigEntity.TaskEntity CreateTaskEntity(){
            DefaultConfigEntity.TaskEntity entity = new DefaultConfigEntity.TaskEntity();
            entity.setTaskType(Const.taskType.redirect);
            entity.setTaskList(taskList);
            entity.setRequestHeader(requestHeader);
            entity.setMethod(method);
            entity.setParam(param);
            return entity;
        }
    }
}
