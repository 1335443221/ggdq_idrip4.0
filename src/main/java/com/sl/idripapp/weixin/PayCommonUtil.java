package com.sl.idripapp.weixin;

import com.sl.common.utils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;

public class PayCommonUtil {
    private static Logger logger = LoggerFactory.getLogger(com.sl.idripapp.weixin.PayCommonUtil.class);

    /**
     * 自定义长度随机字符串
     * @param length
     * @return
     */
    public static String createConceStr(int length) {
        String strs = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String str = "";
        for (int i = 0; i < length; i++) {
            // str +=strs.substring(0, new Random().nextInt(strs.length()));
            char achar = strs.charAt(new Random().nextInt(strs.length() - 1));
            str += achar;
        }
        return str;
    }

    /**
     * 默认16 位随机字符串
     * @return
     */
    public static String CreateNoncestr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    /**
     * 签名工具
     * @date 2014-12-5下午2:29:34
     * @Description：sign签名
     * @param characterEncoding
     *            编码格式 UTF-8
     * @param parameters
     *            请求参数
     * @return
     */
    public static String createSign(String characterEncoding,
                                    Map<String, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Iterator<Entry<String, Object>> it = parameters.entrySet().iterator();
        while (it.hasNext()) {
            Entry <String,Object>entry = (Entry<String,Object>) it.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();//去掉带sign的项
            if (null != value && !"".equals(value) && !"sign".equals(key)
                    && !"key".equals(key)) {
                sb.append(key + "=" + value + "&");
            }
        }
        sb.append("key=" + ConfigUtil.API_KEY);
        //注意sign转为大写
        return MD5.md5(sb.toString(), characterEncoding).toUpperCase();
    }

    /**
     * @date
     * @Description：将请求参数转换为xml格式的string
     * @param parameters
     *            请求参数
     * @return
     */
    public static String getRequestXml(SortedMap<String, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String,Object> entry = (Entry<String,Object>) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            sb.append("<" + key + ">" + value + "</" + key + ">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    public static String setXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
                + "]]></return_code><return_msg><![CDATA[" + return_msg
                + "]]></return_msg></xml>";
    }


    /**
     * 检验API返回的数据里面的签名是否合法
     *
     * @param responseString API返回的XML数据字符串
     * @return API签名是否合法
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static boolean checkIsSignValidFromResponseString(String responseString) {

        try {
            SortedMap<String, Object> map = XMLUtil.doXMLParse(responseString);
            logger.debug(map.toString());
            String signFromAPIResponse = map.get("sign").toString();
            if ("".equals(signFromAPIResponse) || signFromAPIResponse == null) {
                logger.debug("API返回的数据签名数据不存在，有可能被第三方篡改!!!");
                return false;
            }
            logger.debug("服务器回包里面的签名是:" + signFromAPIResponse);
            map.put("sign", "");
            String signForAPIResponse = com.sl.idripapp.weixin.PayCommonUtil.createSign("UTF-8", map);
            if (!signForAPIResponse.equals(signFromAPIResponse)) {
                logger.debug("数据签名验证不通过");
                return false;
            }
            logger.debug("恭喜，数据签名验证通过!!!");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}