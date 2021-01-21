package com.sl.idripapp.weixin2;

import com.github.wxpay.sdk.WXPayConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MyConfig implements WXPayConfig {

    private String appID;
    private String mchID;
    private String key;
    private int httpConnectTimeoutMs;
    private int httpReadTimeoutMs;
    private byte[] certData;

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setHttpConnectTimeoutMs(int httpConnectTimeoutMs) {
        this.httpConnectTimeoutMs = httpConnectTimeoutMs;
    }

    public void setHttpReadTimeoutMs(int httpReadTimeoutMs) {
        this.httpReadTimeoutMs = httpReadTimeoutMs;
    }

    public byte[] getCertData() {
        return certData;
    }

    public void setCertData(byte[] certData) {
        this.certData = certData;
    }


    @Override
    public String getAppID() {
        return appID;
    }

    @Override
    public String getMchID() {
        return mchID;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return httpConnectTimeoutMs;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return httpReadTimeoutMs;
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    /*public MyConfig() throws Exception {
        String certPath = "/path/to/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }
*/
    /*@Override
    public String getAppID() {
        return "wx8888888888888888";
    }
    @Override
    public String getMchID() {
        return "12888888";
    }
    @Override
    public String getKey() {
        return "88888888888888888888888888888888";
    }
   */


    /*@Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }
    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }
*/
}
