package com.sl.common.service;

import com.sl.common.entity.UIDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/10/9 16:34
 * FileName: MongoDBService
 * Description: MongoDB的业务层
 */
@Service("mongoDBService")
public class MongoDBService {
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 收缴费获取fpgMongoDB数据
     * @param map
     * @return
     */
    public List<UIDataVo> getSjfFpgData(Map<String,Object> map) {
        ArrayList<String> deviceList = (ArrayList<String>) map.get("deviceList");
        ArrayList<String> tagList = (ArrayList<String>) map.get("tagList");
        Query query = new Query();
        query.addCriteria(Criteria.where("log_time").gte(String.valueOf(map.get("bdate"))).lte(String.valueOf(map.get("edate"))));
        query.addCriteria(Criteria.where("tag").in(tagList));
        query.addCriteria(Criteria.where("device").in(deviceList));
        if(map.get("tg")!=null){
            query.addCriteria(Criteria.where("tg").is(String.valueOf(map.get("tg"))));
        }
        List<UIDataVo> all = mongoTemplate.find(query, UIDataVo.class, String.valueOf(map.get("code_name")));
        return all;
    }
}
