package sid.bo.common;

import sid.bo.BaseDataEntity;
import sid.bo.annotation.Persistence;

import java.sql.Timestamp;

@Persistence(table = "err_log")
public class Error extends BaseDataEntity {
    Timestamp happenTime;
    String message;
    String remark;

    public Error(){
        this.setAutoCommit(true);
    }

    public Error(Timestamp happenTime,String message,String remark){
        this();
        this.happenTime = happenTime;
        this.message = message;
        this.remark = remark;
    }

    public Timestamp getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Timestamp happenTime) {
        this.happenTime = happenTime;
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
