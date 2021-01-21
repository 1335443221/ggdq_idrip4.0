package com.sl.common.service;

import com.sl.common.config.ConstantConfig;
import com.sl.idripweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: CacheService
 * Description: 缓存类
 */
@Service
public class CacheService {
    @Autowired
    private UserService userService;
    @Autowired
    ConstantConfig constant;

    /**
     * 从session中获取用户信息
     * @param sessionID
     * @return
     */
    @Cacheable("sessionUser")
    public Map<String, Object> getUserCache(String sessionID) {
        return  userService.getUserInfo(sessionID);
    }

}
