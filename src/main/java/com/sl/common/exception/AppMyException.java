package com.sl.common.exception;

import com.sl.common.utils.AppCodeMsg;
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: AppMyException
 * Description: App端自定义异常
 */
public class AppMyException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger(getClass());

    AppCodeMsg cm;
    private String code;
    private String msg;
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public AppMyException(String code) {
        this.code =code;
        this.msg = AppCodeMsg.codeMsg.get(code);
    }


    public AppMyException(String code, String info) {
        this.code =code;
        this.msg = AppCodeMsg.codeMsg.get(code);
        this.info = info;
    }

    public AppMyException(String i, String msg, String info) {
        this.code = i;
        this.msg = msg;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public AppCodeMsg getCm() {
        return cm;
    }

    public void setCm(AppCodeMsg cm) {
        this.cm = cm;
    }

    @Override
    public String toString() {
        return "AppMyException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
