package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface UploadConfigService {

    //获取当前设置的文件上传方式
    public AppResult getActiveUploadConfig(Map<String, Object> map);

    //修改文件上传方式
    public AppResult updateUploadConfig(Map<String, Object> map);
}
