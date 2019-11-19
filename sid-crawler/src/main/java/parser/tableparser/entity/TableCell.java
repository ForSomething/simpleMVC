package parser.tableparser.entity;

public class TableCell {
    private Object content;

    private ContentType contentType;

    public <T> T getContent() {
        return (T)content;
    }

    public void setContent(Object content){
        if(content instanceof Table){
            this.content = content;
            contentType = ContentType.TABLE;
        }else {
            this.content = content == null ? null : content.toString();
            contentType = ContentType.TEXT;
        }
    }

    public ContentType getContentType() {
        return contentType;
    }

    public enum ContentType{
        TABLE,
        TEXT
    }
}
