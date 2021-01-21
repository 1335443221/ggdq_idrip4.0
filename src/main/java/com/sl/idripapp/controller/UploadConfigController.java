package com.sl.idripapp.controller;


import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.UploadConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 文件上传位置配置控制器，可选七牛服务器或本地
 */
@Controller
@RequestMapping("uploadConfig")
public class UploadConfigController {

    @Autowired
    private UploadConfigService uploadConfigService;

    //跳转到文件上传配置页面
    @RequestMapping("uploadConfPage")
    public String goUploadConfigPage(Model model,@RequestParam Map<String, Object> map){
        AppResult result = uploadConfigService.getActiveUploadConfig(map);
        model.addAttribute("active",result.getData());
        return "upload_config";
    }

    //修改文件上传配置
    @RequestMapping("updateUploadConfig")
    @ResponseBody
    public AppResult updateUploadConfig(@RequestParam Map<String, Object> map){
        return uploadConfigService.updateUploadConfig(map);
    }

    //获取当前文件上传配置
    @RequestMapping("getUploadConfig")
    @ResponseBody
    public AppResult getUploadConfig(@RequestParam Map<String, Object> map){
        return uploadConfigService.getActiveUploadConfig(map);
    }

}
