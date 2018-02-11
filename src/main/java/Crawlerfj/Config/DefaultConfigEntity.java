package Crawlerfj.Config;

import Crawlerfj.Common.Const;
import Crawlerfj.Common.StringUtil;
import Crawlerfj.Request.DefaultRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class DefaultConfigEntity {
    //需要爬取的元素的列表
    private ArrayList<ElementEntity> elementList;

    //爬取的url
    private String url;

    //url的请求方式
    private DefaultRequest.Method method;

    //请求参数
    private Object[] param = null;

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

    public Object[] getParam() {
        return param;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMethod(DefaultRequest.Method methond) {
        this.method = methond;
    }

    public void setParam(Object[] param) {
        this.param = param;
    }

    public int getElementEntityCount(){
        return elementList.size();
    }

    public class ElementEntity{
        //选择器层次列表
        //选择器格式与jquery的选择器格式相同
        private LinkedList<String> selectorLevelList;

        //对爬取到的元素实行什么样的操作
        private Const.elementHandleAction handleAction;

        public ElementEntity(){
            selectorLevelList = new LinkedList<String>();
            handleAction = null;
        }

        public LinkedList<String> getSelectorList() {
            return selectorLevelList;
        }

        public Const.elementHandleAction getHandleAction() {
            return handleAction;
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

        //利用这个方法可以保证每个ElementEntity都是合法的
        private boolean isIllegal(){
            if(selectorLevelList.size() == 0 || handleAction == null){
                return false;
            }
            return true;
        }
    }
}
