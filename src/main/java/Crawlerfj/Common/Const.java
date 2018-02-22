package Crawlerfj.Common;

public class Const {
    public enum elementHandleAction{
        redirectTo,
        download,
        getTag,
        getJson,
        handleAsJSON
    }

    public enum contentHandleAction{
        handleAsJSON,
        handleAsXML,
        handleAsHtml,
        handleAsText
    }
}
