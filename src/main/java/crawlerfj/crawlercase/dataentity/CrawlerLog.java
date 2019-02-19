package crawlerfj.crawlercase.dataentity;

import annotation.Table;

import java.sql.Timestamp;

@Table(table = "crawler_log")
public class CrawlerLog extends BaseDataEntity {
    public enum LogType{
        NORMAL,
        ERROR,
        DEBUG,
        WARMING
    }

    public CrawlerLog(){

    }

    public CrawlerLog(Timestamp createTime, LogType logType, String message, String remark){
        this.createTime = createTime;
        this.logType = logType;
        this.message = message;
        this.remark = remark;
    }

    private Timestamp createTime;
    private LogType logType;
    private String message;
    private String remark;

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
