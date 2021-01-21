package com.sl.idripweb.config;

import com.alibaba.fastjson.JSONObject;
import com.sl.common.service.CacheService;
import com.sl.common.config.ConstantConfig;
import com.sl.common.exception.WebMyException;
import com.sl.common.interceptor.ExceptionHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过session获取用户信息
 *
 * @author msw
 */
@Service("mySession")
public class MySession {
    @Autowired
    private ConstantConfig constantSettingProperties;
    @Autowired
    private CacheService cacheServiceImpl;
    private Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);
    // 获取用户信息
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String sessionID = "";
        if (cookies != null && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {
                if ("ove_session".equals(cookies[i].getName())) {
                    sessionID = constantSettingProperties.getOveSession() + cookies[i].getValue();
                }
            }
            if (sessionID == null || "".equals(sessionID) || constantSettingProperties.getOveSession().equals(sessionID)) {
                logger.error("\n========url>>>" + request.getRequestURL().toString() + "\n========Msg>>>登录过(获取SessionID为空) ");
            }
        }
        Map<String, Object> jsonResult=cacheServiceImpl.getUserCache(sessionID);
        //获取用户信息失败 1：未登录 2：redis 服务器异常
        if (jsonResult == null ) {
           throw new WebMyException(450);
        }
        return jsonResult;
    }

    /**
     * 从session中获取厂区
     */
    public ArrayList<Map<String, Object>> getFactory(HttpServletRequest request) {
        Map<String, Object> user = getUserInfo(request);
        Map<String, Object> factory = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (user == null || user.size() <= 0 || user.get("factory_info") == null)
            return list;
        factory = JSONObject.parseObject(user.get("factory_info").toString());
        for (Object value : factory.values()) {
            if (value == null || String.valueOf(value) == "")
                return list;
            Map<String, Object> f = JSONObject.parseObject(String.valueOf(value));
            String id = String.valueOf(f.get("id"));
            String factory_name = String.valueOf(f.get("factory_name"));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("factory_id", id);
            map.put("factory_name", factory_name);
            list.add(map);
        }
        return list;
    }




}
