package crawlerfj.common;

public class Const {
    public enum elementHandleAction{
        download,
        getTag,
    }

    public enum taskType{
        getHtmlElement,
        getContent,
        redirect
    }

    public enum requestMethod{
        GET,
        POST
    }
}
