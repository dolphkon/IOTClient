package com.dolphkon.me.bean;

import java.io.PrintWriter;

/**
 * Created by langstone on 2018/9/13.
 */

public class StatusMsg {
    private String status;
    private String errCode;

    @Override
    public String toString() {
        return "StatusMsg{" +
                "status='" + status + '\'' +
                ", errCode='" + errCode + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
}
