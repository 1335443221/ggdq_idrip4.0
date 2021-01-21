package com.sl.idripapp.service.impl;

import com.sl.common.config.ConstantConfig;
import com.sl.common.utils.AppResult;
import com.sl.common.utils.Jipush;
import com.sl.idripapp.dao.UploadConfigDao;
import com.sl.idripapp.service.UploadConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UploadConfigServiceImpl implements UploadConfigService {

    @Autowired
    ConstantConfig constant;
    @Autowired
    private UploadConfigDao uploadConfigDao;

    //获取当前设置的文件上传方式
    @Override
    public AppResult getActiveUploadConfig(Map<String, Object> map) {
        map.remove("token");
        Map<String, Object> activeUploadConfig = uploadConfigDao.getActiveUploadConfig();
        activeUploadConfig.put("qiniuUrl",constant.getQiniuUrl());
        return AppResult.success(activeUploadConfig);
    }

    //修改文件上传方式
    @Transactional
    @Override
    public AppResult updateUploadConfig(Map<String, Object> map) {
        map.remove("token");
        Integer active_id = Integer.parseInt(String.valueOf(map.get("active_id")));
        int i = uploadConfigDao.updateUploadConfig(active_id);
        //极光推送
        List<String> tagList = new ArrayList<>();
        Map<String,String> extras = new HashMap<>();
        extras.put("type","uploadConfig");
        extras.put("activeId",String.valueOf(map.get("active_id")));
        extras.put("qiniuUrl",constant.getQiniuUrl());
        tagList.add(constant.getJiGaungEnv());
        Jipush.sendToTagListMessage(tagList,"文件上传配置已修改！","",extras,"");
        if(i>0){
            return AppResult.success();
        }else{
            return AppResult.error("1010");
        }
    }
}
