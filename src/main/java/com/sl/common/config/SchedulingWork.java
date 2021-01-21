package com.sl.common.config;

import com.alibaba.fastjson.JSONObject;
import com.sl.common.service.SchedulingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

@Configuration
@EnableScheduling
public class SchedulingWork {
    @Autowired
    private SchedulingService schedulingService;

    /**
     *设备管理  每天凌晨12点半检测是否需要插入任务
     */
    @Scheduled(cron = "0 32 0 * * ?")
    private void insertElcmTaskRrcord(){
        schedulingService.insertElcmTaskRrcord("71");
    }
}
