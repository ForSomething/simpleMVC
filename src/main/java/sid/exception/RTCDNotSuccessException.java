package sid.exception;

public class RTCDNotSuccessException extends RuntimeException {
    private String rtcd;
    private String msg;
    private static final String defaultRtcd = "9999";

    public RTCDNotSuccessException(String msg) {
        super("rtcd=" + defaultRtcd + ";msg = " + msg);
        this.msg = msg;
        this.rtcd = defaultRtcd;
    }

    public RTCDNotSuccessException(String code, String msg) {
        super("rtcd=" + code + ";msg = " + msg);
        this.msg = msg;
        this.rtcd = code;
    }

    public RTCDNotSuccessException(String message, Throwable e) {
        super(message, e);
    }

    public String getMsg() {
        return this.msg;
    }

    public String getRtcd() {
        return this.rtcd;
    }
}
