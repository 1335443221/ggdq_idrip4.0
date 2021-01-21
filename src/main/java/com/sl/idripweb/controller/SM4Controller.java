package com.sl.idripweb.controller;

import com.alibaba.fastjson.JSON;
import com.sl.common.entity.params.SM4Entity;
import com.sl.common.utils.Sm4Util;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashSet;
import java.util.Map;

@Controller
@RequestMapping("/sm4")
public class SM4Controller {

    /**
     * 返回加密数据
     * @return
     */
    @RequestMapping("encrypt")
    @ResponseBody
    public String getPowerAndFeesOfHouse(@RequestParam Map<String,Object> map)throws Exception{
        if (map.get("key")==null||String.valueOf(map.get("key")).equals("")){
            return "缺少key参数，请传国密密钥key";
        }

        SM4Entity sm4=new SM4Entity();
        sm4.setStatDate(map.get("statDate")==null?"":map.get("statDate").toString());
        sm4.setStatType(map.get("statType")==null?"":map.get("statType").toString());
        sm4.setDataValue(map.get("dataValue")==null?"":map.get("dataValue").toString());
        sm4.setDataCode(map.get("dataCode")==null?"":map.get("dataCode").toString());
        sm4.setValid(map.get("valid")==null?"":map.get("valid").toString());
        sm4.setScope(map.get("scope")==null?"":map.get("scope").toString());
        sm4.setInputType(map.get("inputType")==null?"":map.get("inputType").toString());


        String key=String.valueOf(map.get("key"));
        HashSet set =new HashSet();
        set.add("statDate");
        set.add("statType");
        set.add("dataValue");
        set.add("dataCode");
        set.add("valid");
        set.add("scope");
        set.add("inputType");
        Sm4Util.encryptObject(sm4,key,set);
        return JSON.toJSONString(sm4);
    }
}
