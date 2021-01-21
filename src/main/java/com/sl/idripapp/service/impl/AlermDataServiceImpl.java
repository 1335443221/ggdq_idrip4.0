package com.sl.idripapp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sl.common.config.ConstantConfig;
import com.sl.common.utils.*;
import com.sl.idripapp.dao.AlermDataDao;
import com.sl.idripapp.service.AlermDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("alermDataImpl")
public class AlermDataServiceImpl implements AlermDataService {
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    ConstantConfig constant;
    @Autowired
    private AlermDataDao alermDataDao;

    /**
     * 报警列表
     */
    @Override
    public AppResult alermList(Map<String, Object> map) {
        List<Map<String, Object>> list = new ArrayList<>();    //data 多条数据
        if (map.get("pageSize") == null ||map.get("pageNum") == null) {
            map.put("pageNum", 1);
            map.put("pageSize", 20);
        }
        int fromNum = (Integer.parseInt(String.valueOf(map.get("pageNum"))) - 1) * Integer.parseInt(String.valueOf(map.get("pageSize")));
        Map<String, Object> params = new HashMap<String, Object>();   //传参
        params.put("pageSize", map.get("pageSize"));
        params.put("fromNum", fromNum);

        //根据token获取project_id
        params.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   //把时间转换成年月日
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        String mon = sdf.format(m);   //获取过去一个月的时间
        params.put("btime", mon);  //存到条件查询里
        //获取报警列表数据
        ArrayList<Map<String, Object>> alermList = alermDataDao.getAlermData(params);

        Map<String, Object> result = new HashMap<String, Object>();
        if (alermList.size() < Integer.parseInt(map.get("pageSize").toString())) {
            result.put("is_lastpage", true);
        } else {
            result.put("is_lastpage", false);
        }
        for (int i = 0; i < alermList.size(); i++) {
            Map<String, Object> alremData = new HashMap<String, Object>();   //一条报警数据
            alremData.put("category_name", alermList.get(i).get("category_name"));  //报警类别
            alremData.put("log_time", alermList.get(i).get("log_time"));              //报警时间
            alremData.put("location", alermList.get(i).get("location"));          //报警位置
            alremData.put("device_name", alermList.get(i).get("device_name"));      //设备
            alremData.put("is_deal", alermList.get(i).get("is_deal"));        //是否解决 1.解决   0.未解决
            alremData.put("id", alermList.get(i).get("id"));    //报警id
            list.add(alremData);
        }
        result.put("alermlist", list);
        return AppResult.success(result);
    }


    /**
     * 报警详情
     */
    @Override
    public AppResult alermDetail(Map<String, Object> map) {
        Map<String, Object> result = new HashMap();   //返回数据
        if (map.get("id") == null || String.valueOf(map.get("id")) == "") {
            return AppResult.error("1003");
        }
        //报警基础信息
        Map<String, Object> alermData = alermDataDao.getDataById(String.valueOf(map.get("id")));
        //维修登记信息
        Map<String, Object> dealDetail = alermDataDao.queryDealDetail(String.valueOf(map.get("id")));
        //返回数据赋值
        result.put("level", Integer.parseInt(alermData.get("level").toString()));                  //报警等级
        result.put("category_name", alermData.get("category_name") == null ? "" : alermData.get("category_name"));  //报警类别
        result.put("factory_name", alermData.get("factory_name") == null ? "" : alermData.get("factory_name"));    //所属区域
        result.put("location", alermData.get("location") == null ? "" : alermData.get("location"));          //报警位置
        result.put("device_name", alermData.get("device_name") == null ? "" : alermData.get("device_name"));      //设备
        result.put("config_desc", alermData.get("config_desc") == null ? "" : alermData.get("config_desc"));      //报警描述
        result.put("log_time", alermData.get("log_time") == null ? "" : alermData.get("log_time"));              //报警时间
        result.put("value", Double.parseDouble(alermData.get("value").toString()));                  //报警值
        result.put("th", Double.parseDouble(alermData.get("th").toString()));                          //正常值
        result.put("operater", alermData.get("operater") == null ? "" : alermData.get("operater"));              //处理人
        result.put("confirm_time", alermData.get("confirm_time") == null ? "" : alermData.get("confirm_time"));      //处理时间
        if (dealDetail == null || dealDetail.get("repa_msg") == null) {
            result.put("repair_registration", "");//维修登记
        } else {
            result.put("repair_registration", dealDetail.get("repa_msg"));//维修登记
        }
        result.put("is_deal", alermData.get("is_deal"));        //是否解决 1.解决   0.未解决
        result.put("id", Integer.parseInt(alermData.get("id").toString()));              //报警id
        return AppResult.success(result);
    }

    /**
     * 报警处理
     */
    @Override
    @Transactional
    public AppResult alermDispose(Map<String, Object> map) {
        if (map.get("id") == null || map.get("id") == "") {
            return AppResult.error("1003");
        }
        Map<String, Object> map4 = new HashMap<String, Object>();   //返回数据
        //报警基础信息
        Map<String, Object> alermData = alermDataDao.getDataById(String.valueOf(map.get("id")));
        //维修信息
        Map<String, Object> dealDetail = alermDataDao.queryDealDetail(String.valueOf(map.get("id")));
        //如果已解决 返回报警已解决
        if ("1".equals(alermData.get("is_deal").toString())) {
            return AppResult.error("1016");
        }
        Map<String, Object> dealMap = new HashMap<String, Object>();
        dealMap.put("lid", map.get("id"));
        dealMap.put("mark_msg", map.get("msg") == null ? "" : map.get("msg").toString());
        if (map.get("mark_file") != null) {
            dealMap.put("mark_file", map.get("mark_file"));
        }
        //保存处理信息
        if (dealDetail == null) {
            alermDataDao.saveAlermMark(dealMap);
        } else {
            alermDataDao.updateAlermMark(dealMap);
        }
        //处理后插入历史表
        List<Map> list = new ArrayList<>();
        //alerm信息
        Map<String, Object> alerm = alermDataDao.selectAlermById(String.valueOf(map.get("id")));
        alerm.put("operater", JwtToken.getUserName(String.valueOf(map.get("token")))); //操作人姓名
        if (alerm.get("timely_time") == null || "".equals(String.valueOf(alerm.get("timely_time")))) {  //沒有录入信息
            alerm.put("is_timely", 1);  //默认是1 及时处理
        } else {
            //判断是否及时处理
            Date log_time = DateUtil.parseStrToDate(String.valueOf(alerm.get("log_time")), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
            int timely_time = Integer.parseInt(String.valueOf(alerm.get("timely_time")));
            Date date = DateUtil.addDate(log_time, 0, 0, 0, timely_time, 0, 0, 0);
            if (dateUtil.isEffectiveDate(new Date(), log_time, date, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS)) {
                alerm.put("is_timely", 1);  //及时处理
            } else {
                alerm.put("is_timely", 0);  //不是及时处理
            }
        }
        //插入历史表
        alermDataDao.insertHistory(alerm);
        //删除logs表数据
        int data = alermDataDao.deleteById(String.valueOf(map.get("id")));
        if (data == 0) {
            return AppResult.error("1017");
        }
        //返回最新数据
        return alermDetail(map);
    }

/////////////////===========fire================//

    /**
     * 报警-报警列表
     *
     * @param map
     * @return
     */
    @Override
    public AppResult fireAlermList(Map<String, Object> map) {
        if (map.get("pageSize") == null || map.get("pageSize") == "" || map.get("pageNum") == "" || map.get("pageNum") == null) {
            map.put("pageNum", 1);
            map.put("pageSize", 20);
        }
        int fromNum = (Integer.parseInt(String.valueOf(map.get("pageNum"))) - 1) * Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("endTime", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));
        Date beginData = DateUtil.addDate(new Date(), 0, -6, 0, 0, 0, 0, 0);
        map.put("beginTime", DateUtil.parseDateToStr(beginData, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));
        map.remove("token");

        Map<String, Object> result = new HashMap<>();
        int pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        int total = alermDataDao.fireAlermListCount(map);
        if ((fromNum + pageSize) >= total) {
            result.put("is_lastpage", true);
        } else {
            result.put("is_lastpage", false);
        }
        result.put("alermlist", alermDataDao.fireAlermList(map));
        return AppResult.success(result);
    }


    /**
     * 报警-火灾报警处理
     *
     * @param map
     * @return
     */
    @Override
    @Transactional
    public AppResult fireAlermDispose(Map<String, Object> map) {
        if (map.get("id") == null || map.get("id") == "") {
            return AppResult.error("1003");
        }
        if (map.get("fileUrl") == null || String.valueOf(map.get("fileUrl")).equals("")) {
            map.put("mark_file", null);
        }
        //报警处理
        alermDispose(map);
        return fireAlermDetail(map);
    }

    /**
     * 报警-火灾报警详情
     *
     * @param map
     * @return
     */
    @Override
    public AppResult fireAlermDetail(Map<String, Object> map) {
        if (map.get("id") == null || map.get("id") == "") {
            return AppResult.error("1003");
        }
        Map<String, Object> result = alermDataDao.fireAlermDetail(map);
        if ("0".equals(String.valueOf(result.get("is_deal")))) {
            result.put("confirm_msg", "");
            result.put("confirm_url", new ArrayList<>());
        } else {
            Map<String, Object> dealDetail = alermDataDao.queryAlermDealDetail(map);
            result.put("confirm_msg", dealDetail.get("mark_msg"));
            if (dealDetail.get("mark_file") == null) {
                result.put("confirm_url", new ArrayList<>());
            } else {
                result.put("confirm_url", String.valueOf(dealDetail.get("mark_file")).split(","));
            }
        }
        return AppResult.success(result);
    }


    /**
     * 报警-火灾报警等级列表
     *
     * @param map
     * @return
     */

    @Override
    public AppResult getAlermLevel(Map<String, Object> map) {
        List<Map> list = JSONObject.parseArray(constant.getAlermLevel(), Map.class);
        return AppResult.success(list);
    }

    /**
     * 报警-火灾报警类型
     *
     * @param map
     * @return
     */
    @Override
    public AppResult getFireCategoryRelation(Map<String, Object> map) {
        ArrayList<HashMap<String, Object>> resultlist = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> list0 = new ArrayList<HashMap<String, Object>>();
        ArrayList<String> templist2 = new ArrayList<String>();
        map.put("alerm_type_id", 6);
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        ArrayList<HashMap<String, Object>> CateGoryRealtion = alermDataDao.getCategoryRelation(map);
        if (CateGoryRealtion != null && CateGoryRealtion.size() > 0) {
            ArrayList<String> tmpList = new ArrayList<String>();
            for (int i = 0; i < CateGoryRealtion.size(); i++) {
                if (CateGoryRealtion.get(i) != null && CateGoryRealtion.get(i).get("cate_name3") != null) {
                    ArrayList<Object> list1 = new ArrayList<Object>();
                    String c3name = CateGoryRealtion.get(i).get("cate_name3").toString();
                    String c3id = CateGoryRealtion.get(i).get("c3id").toString();
                    for (int j = 0; j < CateGoryRealtion.size(); j++) {
                        String id = CateGoryRealtion.get(j).get("id").toString();
                        String c3name1 = CateGoryRealtion.get(j).get("cate_name3").toString();
                        String category_name = CateGoryRealtion.get(j).get("category_name").toString();
                        if (!tmpList.contains(c3name) && c3name.equals(c3name1)) {
                            HashMap<String, Object> myMap = new HashMap<String, Object>();
                            myMap.put("name", category_name);
                            myMap.put("id", id);
                            list1.add(myMap);
                        }
                    }
                    if (!tmpList.contains(c3name)) {
                        HashMap<String, Object> c3Map1 = new HashMap<String, Object>();
                        c3Map1.put("name", c3name);
                        c3Map1.put("id", c3id);
                        c3Map1.put("nodes", list1);
                        list0.add(c3Map1);
                        tmpList.add(c3name);
                    }
                }
            }
        }
        return AppResult.success(list0);
    }


    /**
     * 报警-火灾标签列表
     *
     * @param map
     * @return
     */
    @Override
    public AppResult getFireTagByDeviceId(Map<String, Object> map) {
        ArrayList<Map<String, Object>> result = alermDataDao.getFireTagByDetectorId(map);
        return AppResult.success(result);
    }


    /**
     * 报警-火灾报警配置新增
     *
     * @param map
     * @return
     */
    @Override
    @Transactional
    public AppResult fireAlConfSave(Map<String, Object> map) {
        if (map.get("device_id") == null || map.get("tag") == null) {
            return AppResult.error("1003");
        }
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("alerm_type_id", 6);  //火灾报警默认是6
        ArrayList<Map<String, Object>> list = alermDataDao.selectIsExistConfig(map);
        int result = 0;
        if (list.size() > 0) { //已存在
            result = alermDataDao.fireAlConfUpdate(map); //修改
        } else {
            ArrayList<HashMap<String, Object>> tg_id = alermDataDao.getTgidByRelation_id(map);
            if (tg_id != null && tg_id.size() > 0 && tg_id.get(0).get("tg_id") != null) {
                map.put("tg_id", tg_id.get(0).get("tg_id").toString());
            }
            result = alermDataDao.fireAlConfSave(map); //保存
        }
        if (result > 0) {
            return AppResult.success();
        } else {
            return AppResult.error("1010");
        }
    }


    /**
     * 报警-火灾报警配置修改
     *
     * @param map
     * @return
     */
    @Override
    @Transactional
    public AppResult fireAlConfUpdate(Map<String, Object> map) {
        map.put("alerm_type_id", 6);
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        int result = alermDataDao.fireAlConfUpdate(map);
        if (result > 0) {
            return AppResult.success();
        } else {
            return AppResult.error("1010");
        }
    }

    /**
     * 报警-火灾报警配置信息
     *
     * @param map
     * @return
     */
    @Override
    public AppResult fireAlConfData(Map<String, Object> map) {
        if (map.get("device_id") == null || map.get("tag") == null) {
            return AppResult.error("1003");
        }
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        ArrayList<Map<String, Object>> list = alermDataDao.fireAlConfData(map);
        if (list.size() > 0) {
            Map<String, Object> result = list.get(0);
            if (result.get("confLevel") != null) {
                result.put("level_name", String.valueOf(result.get("confLevel")));
            } else {
                result.put("level_name", "");
            }
            return AppResult.success(result);
        } else {
            return AppResult.success(new JSONObject());
        }
    }



    /**
     * 处理全部
     *
     * @param map
     * @return
     */
    @Override
    public int alermDisposeAll(Map<String, Object> map) {
        //先查询、String lid , String msg,String uname
        String msg = map.get("msg") == null ? "" : map.get("msg").toString();
        String uname = map.get("uname") == null ? "" : map.get("uname").toString();
        List<Map<String,Object>> alermList=alermDataDao.getNoDealAlermData(map);
        int result=0;

        List<Integer> idList=new ArrayList<>(); //logs的id集合
        List<Map<String,Object>> mesgList=new ArrayList<>(); //需要插入的mesg集合
        List<Map> historyList=new ArrayList<>(); //需要插入的历史集合
        Map<String,Object> alerm=new HashMap<>();//单个报警信息
        Map<String,Object> mesg=new HashMap<>();//单个mesg信息
        int lid=0;  //报警id
        for(int i=0;i<alermList.size();i++){
            //处理后插入历史表
            alerm=alermList.get(i);
            mesg=new HashMap<>();
            lid=Integer.parseInt(String.valueOf(alerm.get("id")));
            mesg.put("lid",lid);
            mesg.put("msg", msg);
            mesgList.add(mesg);
            idList.add(lid);
            alerm.put("operater",uname);
            if(alerm.get("timely_time")==null||"".equals(String.valueOf(alerm.get("timely_time")))){  //沒有录入信息
                alerm.put("is_timely",1);  //默认是1 及时处理
            }else{
                Date log_time=DateUtil.parseStrToDate(String.valueOf(alerm.get("log_time")),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
                int timely_time=Integer.parseInt(String.valueOf(alerm.get("timely_time")));
                Date date=DateUtil.addDate(log_time,0,0,0,timely_time,0,0,0);
                if(dateUtil.isEffectiveDate(new Date(),log_time,date,DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS)){
                    alerm.put("is_timely",1);  //及时处理
                }else{
                    alerm.put("is_timely",0);  //不是及时处理
                }
            }
            historyList.add(alerm);
        }
        if(mesgList.size()>0){
            alermDataDao.deleteAllMesg(idList);
            alermDataDao.insertAllMesg(mesgList);
        }
        if(historyList.size()>0){
            alermDataDao.insertAllHistory(historyList);
            result=alermDataDao.deleteAllLogs(idList);
        }
        return result;
    }

}
