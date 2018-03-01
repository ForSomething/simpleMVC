package Crawlerfj.Config.DefaultConfig;

import Crawlerfj.Common.Const;

import java.util.ArrayList;

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
        //私有化构造函数，只有调用TaskBuilder的CreateDoRedirectTask方法才能实例化此类对象
        private DoRedirectTaskBuilder(){taskList = new ArrayList<DefaultConfigEntity.TaskEntity>();}

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

        public DefaultConfigEntity.TaskEntity CreateTaskEntity(){
            DefaultConfigEntity.TaskEntity entity = new DefaultConfigEntity.TaskEntity();
            entity.setTaskType(Const.taskType.redirect);
            entity.setTaskList(taskList);
            return entity;
        }
    }
}
