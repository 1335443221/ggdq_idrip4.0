package com.sl.idripweb.dao;

import com.sl.idripweb.entity.WebUIDataVo;
import com.sl.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WebUIDataDao {
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<WebUIDataVo> getUIData (Map<String,Object>map) {
        String device=String.valueOf(map.get("device"));
        String tg=String.valueOf(map.get("tg_name"));
        String project_name=String.valueOf(map.get("project_name"));
        String bdate=String.valueOf(map.get("bdate"));
        Query query = new Query();
        //若为传入时间参数初始化时间
        if(bdate==null||"".equals(bdate)||"null".equals(bdate))bdate=DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.parseStrToDate(bdate, DateUtil.DATE_FORMAT_YYYY_MM_DD));
        cal.add(Calendar.DATE, 1);
        //结束时间
        String edate = DateUtil.getFormateDateStr(cal, DateUtil.DATE_FORMAT_YYYY_MM_DD);
        query.addCriteria(Criteria.where("device").is(device));
        query.addCriteria(Criteria.where("tg").is(tg));
        query.addCriteria(Criteria.where("log_time").gte(bdate).lte(edate));
        query.addCriteria(Criteria.where("tag").in("uab","ubc","uca","ia","ib","ic"));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"log_time")));
        List<WebUIDataVo> all = mongoTemplate.find(query,WebUIDataVo.class, project_name);
        return all;
    }

    /**
     * 获取火灾APP的MongoDB数据
     * @param map
     * @return
     */
    public List<WebUIDataVo> getFireUIData(Map<String,Object>map) {
        Query query = new Query();
        query.addCriteria(Criteria.where("device").is(String.valueOf(map.get("device"))));
        query.addCriteria(Criteria.where("tg").is(String.valueOf(map.get("tg"))));
        query.addCriteria(Criteria.where("log_time").gte(String.valueOf(map.get("bdate"))).lte(String.valueOf(map.get("edate"))));
        query.addCriteria(Criteria.where("tag").is(String.valueOf(map.get("tag"))));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"log_time")));
        List<WebUIDataVo> all = mongoTemplate.find(query,WebUIDataVo.class, String.valueOf(map.get("code_name")));
        return all;
    }

    /**
     * 收缴费获取MongoDB数据
     * @param map
     * @return
     */
    public List<WebUIDataVo> getSjfLadderData(Map<String,Object>map) {
        Query query = new Query();
        query.addCriteria(Criteria.where("log_time").gte(String.valueOf(map.get("bdate"))).lte(String.valueOf(map.get("edate"))));
        query.addCriteria(Criteria.where("tag").is(String.valueOf(map.get("tag"))));
        if(map.get("device")!=null){
            query.addCriteria(Criteria.where("device").is(String.valueOf(map.get("device"))));
        }
        if(map.get("tg")!=null){
            query.addCriteria(Criteria.where("tg").is(String.valueOf(map.get("tg"))));
        }
        List<WebUIDataVo> all = mongoTemplate.find(query,WebUIDataVo.class, String.valueOf(map.get("code_name")));
        return all;
    }
    /**
     * 收缴费获取fpgMongoDB数据
     * @param map
     * @return
     */
    public List<WebUIDataVo> getSjfFpgData(Map<String,Object>map) {
        ArrayList<String> deviceList = (ArrayList<String>) map.get("deviceList");
        ArrayList<String> tagList = (ArrayList<String>) map.get("tagList");
        Query query = new Query();
        query.addCriteria(Criteria.where("log_time").gte(String.valueOf(map.get("bdate"))).lte(String.valueOf(map.get("edate"))));
        query.addCriteria(Criteria.where("tag").in(tagList));
        query.addCriteria(Criteria.where("device").in(deviceList));
        if(map.get("tg")!=null){
            query.addCriteria(Criteria.where("tg").is(String.valueOf(map.get("tg"))));
        }
        List<WebUIDataVo> all = mongoTemplate.find(query,WebUIDataVo.class, String.valueOf(map.get("code_name")));
        return all;
    }

    /**
     * 收缴费根据设备列表，通讯机列表、时间和标签查询电表读数
     * @param map
     * @return
     */
    public List<WebUIDataVo> getSjfReadout(Map<String,Object>map) {
        ArrayList<String> deviceList = (ArrayList<String>) map.get("deviceList");
        ArrayList<String> tgList = (ArrayList<String>) map.get("tgList");
        Query query = new Query();
        query.addCriteria(Criteria.where("log_time").gte(String.valueOf(map.get("bdate"))).lte(String.valueOf(map.get("edate"))));
        query.addCriteria(Criteria.where("tag").is("ep"));
        query.addCriteria(Criteria.where("device").in(deviceList));
//        query.addCriteria(Criteria.where("tg").in(tgList));
        List<WebUIDataVo> all = mongoTemplate.find(query,WebUIDataVo.class, String.valueOf(map.get("code_name")));
        return all;
    }

}
