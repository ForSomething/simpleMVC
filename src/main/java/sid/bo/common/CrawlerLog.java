package sid.bo.common;


import sid.bo.BaseDataEntity;
import sid.bo.annotation.Persistence;

import java.sql.Timestamp;
import java.util.List;

@Persistence(table = "crawler_log")
public class CrawlerLog extends BaseDataEntity {
    public enum LogType{
        NORMAL,
        ERROR,
        DEBUG,
        WARMING,
        NOTE
    }

    public CrawlerLog(Timestamp createTime, LogType logType, String message, String remark){
        this.createTime = createTime;
        this.logType = logType;
        this.message = message;
        this.remark = remark;
    }

    public static List<CrawlerLog> load(Object cond) throws Exception {
        return select(cond, CrawlerLog.class);
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
