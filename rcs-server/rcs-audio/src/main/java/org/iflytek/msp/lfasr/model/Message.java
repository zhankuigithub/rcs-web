package org.iflytek.msp.lfasr.model;

public class Message {

    private int ok = 0;
    private int errNo;
    private String failed;
    private String data;

    public Message() {
    }

    public int getOk() {
        return this.ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public String getFailed() {
        return this.failed;
    }

    public void setFailed(String failed) {
        this.failed = failed;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getErrNo() {
        return this.errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public String toString() {
        return String.format("ok:%s errNo:%s failed:%s data:%s", this.ok, this.errNo, this.failed, this.data);
    }
}
