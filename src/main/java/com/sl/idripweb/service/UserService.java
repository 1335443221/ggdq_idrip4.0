package com.sl.idripweb.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sl.common.config.RedisConfig;
import com.sl.common.exception.WebMyException;
import com.sl.common.utils.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.phprpc.util.AssocArray;
import org.phprpc.util.PHPSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserService {
    @Autowired
    private RedisConfig redisConfig;
    @Autowired
    private RedisPoolUtil RedisPoolUtil;

    /**
     * 根据sessionID 获取用户信息
      * @param sessionID
     * @return
     */
    public Map<String, Object> getUserInfo(String sessionID) {
        try {
            Logger logger = Logger.getLogger(getClass());
            String userStr = RedisPoolUtil.get(sessionID, redisConfig.getDatabase6());
            if (userStr == null || "".equals(userStr) || "null".equals(userStr)){
                logger.error("================sessionID>>>"+sessionID+"====userStr>>>"+userStr);
                throw new WebMyException(450, "sessionID为空");
            }

           return JSONObject.parseObject(userStr);
        } catch (Exception e) {
            throw new WebMyException(450, "center获取用户异常", e.toString());
        }
    }

    /**
     * php 反序列化
     * @param content
     * @return
     */
    public  Object unserialize(String content){
        PHPSerializer p = new PHPSerializer();
        Object t = null;
        try { t = p.unserialize(content.getBytes());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * php 序列化
     * @param content
     * @return
     */
    public  byte[] serialize(Object content){
        PHPSerializer p = new PHPSerializer();
        byte[] t = null;
        try { t = p.serialize(content);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 解析session
     * @param sessionData
     * @return
     */
    public JSONObject parseSessopm(String sessionData){
        JSONObject resutn_data=new JSONObject();
        int offset=0;
        while (offset<sessionData.length()){
            if (!sessionData.substring(offset).contains("|")){
                System.out.println("============异常============"+sessionData.substring(offset));
            }
            int pos = StringUtils.indexOf(sessionData, "|" , offset);
            int num=pos-offset;
            String varname=sessionData.substring(offset).substring(0,num);
            offset+=num+1;
            Object data=unserialize(sessionData.substring(offset));
            Object serializeData=unserialize(sessionData.substring(offset));
            if (data instanceof byte[]){
                data=new String((byte[])data);
            }
            if (data instanceof AssocArray){
                AssocArray array = (AssocArray) data;
                HashMap hashmap = array.toLinkedHashMap();
                if (hashmap.size()!=0){
                    Map<String,Object> map = new HashMap<String,Object>();
                    for (Object key : hashmap.keySet()) {
                        if (hashmap.get(key) instanceof byte[]) {
                            map.put((String) key, new String((byte[]) hashmap.get(key)));
                        } else if (hashmap.get(key) instanceof AssocArray){

                            AssocArray valueList = (AssocArray) hashmap.get(key);
                            HashMap value = valueList.toLinkedHashMap();
                            Map<String,Object> valueMap = new HashMap<String,Object>();
                            for (Object valueKey : value.keySet()) {
                                if (value.get(valueKey) instanceof byte[]) {
                                    valueMap.put((String) valueKey, new String((byte[]) value.get(valueKey)));
                                }else if (value.get(valueKey) instanceof AssocArray){
                                    AssocArray valueValueList = (AssocArray) value.get(valueKey);
                                    HashMap valueValue = valueValueList.toLinkedHashMap();
                                    Map<String,Object> valueValueMap = new HashMap<String,Object>();
                                    for (Object valueValueKey : valueValue.keySet()) {
                                        if (valueValue.get(valueValueKey) instanceof byte[]) {
                                            valueValueMap.put( valueValueKey.toString(), new String((byte[]) valueValue.get(valueValueKey)));
                                        }else{
                                            valueValueMap.put(valueValueKey.toString(), valueValue.get(valueValueKey));
                                        }
                                    }

                                    valueMap.put((String) valueKey,valueValueMap);
                                }
                            }
                            map.put(key.toString(), valueMap);

                        }
                    }
                    data=map;
                }else{
                    List<Object> listResult = new ArrayList<Object>();
                    for (int i = 0; i < array.size(); i++) {
                        listResult.add(new String((byte[])array.get(i)));
                    }
                    data=listResult;
                }

            }
            resutn_data.put(varname,data);
            offset+=new String((byte[])serialize(serializeData)).length();
        }
       return resutn_data;

    }
}
