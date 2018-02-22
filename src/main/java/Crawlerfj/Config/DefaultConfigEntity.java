package Crawlerfj.Config;

import Crawlerfj.Common.Const;
import Crawlerfj.Common.StringUtil;
import Crawlerfj.Request.DefaultRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class DefaultConfigEntity {
    //需要爬取的元素的列表
    private ArrayList<ElementEntity> elementList;

    //爬取的url
    private String url;

    //url的请求方式
    private DefaultRequest.Method method;

    //请求参数
    private Map<String,String> param = null;

    //自定义的请求头
    private Map<String,String> requestHeader = null;

    //对爬取到的内容进行什么样的处理,默认当做html处理
    private Const.contentHandleAction handleAction = Const.contentHandleAction.handleAsHtml;

    public DefaultConfigEntity(){
        elementList = new ArrayList<ElementEntity>();
    }

    public ArrayList<ElementEntity> getElementEntityList() {
        return elementList;
    }

    public DefaultConfigEntity addElementEntity(ElementEntity entity){
        if(entity == null || !entity.isIllegal()){
            return this;
        }
        elementList.add(entity);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DefaultRequest.Method getMethod() {
        return method;
    }

    public Map<String,String> getParam() {
        return param;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMethod(DefaultRequest.Method method) {
        this.method = method;
    }

    public void setParam(Map<String,String> param) {
        this.param = param;
    }

    public int getElementEntityCount(){
        return elementList.size();
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public Const.contentHandleAction getHandleAction() {
        return handleAction;
    }

    public void setHandleAction(Const.contentHandleAction handleAction) {
        this.handleAction = handleAction;
    }

    public class ElementEntity{
        //选择器层次列表
        //选择器格式与jquery的选择器格式相同
        private LinkedList<String> selectorLevelList;

        //对爬取到的元素实行什么样的操作
        private Const.elementHandleAction handleAction;

        //用以保存附件的文件夹的路径
        private String downloadFolderPath;

        public ElementEntity(){
            selectorLevelList = new LinkedList<String>();
            handleAction = null;
            //指向上一层目录
            File dir = new File("..") ;
            try {
                downloadFolderPath = dir.getCanonicalPath() + "//download";
            } catch (IOException e) {
                //这个catch块应该是不可能进入的
                downloadFolderPath = "c://download";
            }
        }

        public LinkedList<String> getSelectorList() {
            return selectorLevelList;
        }

        public Const.elementHandleAction getHandleAction() {
            return handleAction;
        }

        public String getDownloadFolderPath() {
            return downloadFolderPath;
        }

        public ElementEntity nextLevelSelector(String selector) {
            if(StringUtil.IsNullOrWihtespace(selector)){
                return this;
            }
            selectorLevelList.add(selector);
            return this;
        }

        public void setHandleAction(Const.elementHandleAction handleAction) {
            this.handleAction = handleAction;
        }

        public void setDownloadFolderPath(String downloadFolderPath) {
            this.downloadFolderPath = downloadFolderPath;
        }

        //利用这个方法可以保证每个ElementEntity都是合法的
        private boolean isIllegal(){
            if(selectorLevelList.size() == 0 || handleAction == null){
                return false;
            }
            return true;
        }
    }
}
