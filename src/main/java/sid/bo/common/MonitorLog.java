package sid.bo.common;

import sid.bo.BaseDataEntity;
import sid.bo.annotation.Persistence;

import java.sql.Date;

@Persistence(table = "monitor_log")
public class MonitorLog extends BaseDataEntity {
//    @Persistence(column = "CREATE_TIME")
//    private Date createTime;
    private String state;
    private String note;

    public MonitorLog(Date createTime,String state,String note){
//        this.createTime = createTime;
        this.state = state;
        this.note = note;
        this.setAutoCommit(true);
    }

//    public Date getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(Date createTime) {
//        this.createTime = createTime;
//    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
