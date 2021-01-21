package com.sl.common.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.sl.common.config.ConstantConfig;
import com.sl.common.dao.CommonDao;
import com.sl.common.dao.SchedulingDao;
import com.sl.common.entity.ElcmTaskRecord;
import com.sl.common.entity.SjfFpgProjectSet;
import com.sl.common.utils.DateUtil;
import com.sl.common.utils.Jipush;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("SchedulingService")
public class SchedulingService {
    @Autowired
    private SchedulingDao schedulingDao;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private ConstantConfig constantConfig;
    @Autowired
    private CommonService commonService;
    private Logger logger = Logger.getLogger(getClass());

    /**
     * 设备管理--按照维保计划内容  定时插入维保工单  Task→TaskRecord (全部项目  高新已经在分支上跑)
     * @param
     * @return
     */
    public void insertElcmTaskRrcord(String project_id) {
        String oddNumber = "WB" + DateUtil.parseDateToStr(new Date(), dateUtil.DATE_FORMAT_YYMMDD);  //年年月月日日  工单前缀
        //今天
        String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
        //一个月之后
        Date month_end_date=DateUtil.addDate(new Date(),0,1,0,0,0,0,0);
        String month_end_time = DateUtil.parseDateToStr(month_end_date, DateUtil.DATE_FORMAT_YYYY_MM_DD);
        //一个月之后的下一天
        Date month_next_date=DateUtil.addDate(month_end_date,0,0,1,0,0,0,0);
        String month_next_time = DateUtil.parseDateToStr(month_next_date, DateUtil.DATE_FORMAT_YYYY_MM_DD);  //明天

        Date plan_end_date=DateUtil.addDate(new Date(),0,0,1,0,0,0,0);

        String plan_end_time = DateUtil.parseDateToStr(plan_end_date, DateUtil.DATE_FORMAT_YYYY_MM_DD);  //明天
            String oddNumberSuffix = schedulingDao.elcmTaskRecordNumber(oddNumber,project_id);  //工单后缀
            int number = 1;
            if (oddNumberSuffix == null || oddNumberSuffix.equals("")) {  //证明没有
                number = 1;   //没有后缀的时候 为 001第一号
            } else {
                number = Integer.parseInt(oddNumberSuffix)+1; //有后缀  下一位
            }
            List<Map<String, Object>> insertList = new ArrayList<>();  //插入数据
            List<ElcmTaskRecord> taskList = schedulingDao.getElcmTaskRelation(project_id);  //获取所有关系列表
            for (int j = 0; j < taskList.size(); j++) {  //遍历结果集
                ElcmTaskRecord task = taskList.get(j);
                //如果是启动状态
                if (task.getIsStart() == 1) {
                    Date beginTime = DateUtil.parseStrToDate(task.getBeginTime(), DateUtil.DATE_FORMAT_YYYY_MM_DD); //开始时间
                    Date endTime = DateUtil.parseStrToDate(task.getEndTime(), DateUtil.DATE_FORMAT_YYYY_MM_DD); //结束时间
                    //如果一个月之后在计划开始 和 结束之中
                    if (dateUtil.isEffectiveDate(month_end_date, beginTime, endTime, DateUtil.DATE_FORMAT_YYYY_MM_DD)) {
                        //如果重复并且没有记录 证明审批通过时 任务工单还没开始生成时
                        if (task.getIsRepeat() == 1 && task.getPlanStartTime() == null) {
                            Map<String, Object> insertMap = new HashMap<>();
                            insertMap.put("plan_start_time", month_end_time + " 08:00:00");
                            insertMap.put("plan_end_time", month_next_time + " 08:00:00");
                            insertMap.put("original_plan_start_time", month_end_time + " 08:00:00");
                            insertMap.put("status", 1);
                            insertMap.put("project_id", project_id);
                            insertMap.put("relation_id", task.getRelationId());
                            insertMap.put("record_number", oddNumber + String.format("%03d", number));
                            insertList.add(insertMap);
                            number++; //单号增加一位
                        }
                        //如果重复并且有记录 需要判断是否需要插入下一条
                        if (task.getIsRepeat() == 1 && task.getPlanStartTime() != null) {
                            //上一次计划维保时间
                            Date planStartTime = DateUtil.parseStrToDate(task.getPlanStartTime(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
                            String unit=task.getMaintainIntervalsUnit(); //周期单位
                            int cycle=task.getMaintainIntervals(); //周期
                            Date nextTime=new Date();  //下一次维保日期
                            switch (unit) {
                                case "日":
                                    nextTime=DateUtil.addDate(new Date(),0,0,cycle,0,0,0,0);
                                    break;
                                case "周":
                                    nextTime=DateUtil.addDate(new Date(),0,0,cycle*7,0,0,0,0);
                                    break;
                                case "月":
                                    nextTime=DateUtil.addDate(new Date(),0,cycle,0,0,0,0,0);
                                    break;
                                case "年":
                                    nextTime=DateUtil.addDate(new Date(),cycle,0,0,0,0,0,0);
                                    break;
                            }
                            String nextStartTime= DateUtil.parseDateToStr(nextTime,DateUtil.DATE_FORMAT_YYYY_MM_DD); //下一次开始时间
                            //一个月之后==下一次计划维保时间  生成下次维保工单
                            if(month_end_time.contains(nextStartTime)){
                                Date begin_next_date=DateUtil.addDate(nextTime,0,0,1,0,0,0,0);
                                String begin_next_time = DateUtil.parseDateToStr(begin_next_date, DateUtil.DATE_FORMAT_YYYY_MM_DD);

                                Map<String, Object> insertMap = new HashMap<>();
                                insertMap.put("plan_start_time", nextStartTime + " 08:00:00");
                                insertMap.put("plan_end_time", begin_next_time + " 08:00:00");
                                insertMap.put("original_plan_start_time", nextStartTime + " 08:00:00");
                                insertMap.put("status", 1);
                                insertMap.put("project_id", project_id);
                                insertMap.put("relation_id", task.getRelationId());
                                insertMap.put("record_number", oddNumber + String.format("%03d", number));
                                insertList.add(insertMap);
                                number++; //单号增加一位
                            }
                        }
                    }
                }
            }
            if(insertList.size()>0){
                schedulingDao.insertAllElcmTaskRecord(insertList); //插入工单
            }
    }

    /**
     * 报警推送 ---获取未推送的数据 给app推送
     * @return
     */
    public ArrayList<Map<String, Object>> getLogsByIsPush(String project_id){
        return schedulingDao.getLogsByIsPush(project_id);
    }

    /**
     * 报警推送 --- 报警推送APP 设置为已推送
     * @param id
     */
    public void setLogsPushed(String id){
        schedulingDao.setLogsPushed(id);
    }

    /**
     * 报警推送 --- 推送
     */
    public void alermPush(Map<String, Object> map) {
        String id=String.valueOf(map.get("id"));

        if(map.get("title")!=null&&map.get("project_id")!=null){
            String title=map.get("title").toString();
            List<String> tags=new ArrayList<>();
            tags.add(constantConfig.getJiGaungEnv());
            tags.add("projectId="+map.get("project_id"));
            JsonObject j=new JsonObject();
            if("fire_detector".equals(String.valueOf(map.get("device_type")))){ //如果是火灾 添加type设备类型为火灾
                tags.add("type="+map.get("device_type"));
                j.addProperty("type",map.get("device_type").toString());
            }else{
                tags.add("role=");
                j.addProperty("type","");
            }
            j.addProperty("log_time", map.get("log_time").toString());
            j.addProperty("id",id);
            String a= Jipush.sendToGroupTagList(tags,title, "data",j, "报警推送");
        }
    }



    /**
     * 收缴费 --- 检测电费设置 有无更新
     */
    public void updateSjfFuture(String project_id) {
        Map<String, Object> map = new HashMap<>();
        String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
        map.put("today", today);
        map.put("project_id", project_id);
        ArrayList<Map<String, Object>> elecSettingList = schedulingDao.getElecSettingFutureByProjectId(map);
        for (int i = 0; i < elecSettingList.size(); i++) {
            schedulingDao.updateAllElecSetting(elecSettingList.get(i));
        }
        logger.info(today + "更新电费设置成功!");
        //=================
        ArrayList<Map<String, Object>> ladderList = schedulingDao.getLadderFutureByProjectId(map);
        for (int i = 0; i < ladderList.size(); i++) {
            schedulingDao.updateAllLadder(ladderList.get(i));
        }
        logger.info(today + "更新阶梯设置成功!");
        //==================
        ArrayList<SjfFpgProjectSet> fpgList = schedulingDao.getFpgFutureByProjectId(map);
        for (int i = 0; i < fpgList.size(); i++) {
            SjfFpgProjectSet sjfFpgProjectSet = fpgList.get(i);
            schedulingDao.updateAllFpg(sjfFpgProjectSet);

            //修改峰平谷脚本数据
            String peak = sjfFpgProjectSet.getPeak(); //峰
            String plain = sjfFpgProjectSet.getPlain(); //平
            String valley = sjfFpgProjectSet.getValley(); //谷
            Map<String, Object> fpgMap = new HashMap<>();
            fpgMap.put("project_id", sjfFpgProjectSet.getProjectId());
            fpgMap.put("time_type", "peak");
            if ("".equals(peak)) {
                fpgMap.put("time_str", "");
            } else {
                fpgMap.put("time_str", getFpgSetType(peak));
            }
            if (schedulingDao.getFpgProjectSet(fpgMap).size() == 0) {
                schedulingDao.addFpgProjectSet(fpgMap);
            } else {
                schedulingDao.updateFpgProjectSet(fpgMap);
            }

            fpgMap.put("time_type", "plain");
            if ("".equals(plain)) {
                fpgMap.put("time_str", "");
            } else {
                fpgMap.put("time_str", getFpgSetType(plain));
            }
            if (schedulingDao.getFpgProjectSet(fpgMap).size() == 0) {
                schedulingDao.addFpgProjectSet(fpgMap);
            } else {
                schedulingDao.updateFpgProjectSet(fpgMap);
            }

            fpgMap.put("time_type", "valley");
            if ("".equals(valley)) {
                fpgMap.put("time_str", "");
            } else {
                fpgMap.put("time_str", getFpgSetType(valley));
            }
            if (schedulingDao.getFpgProjectSet(fpgMap).size() == 0) {
                schedulingDao.addFpgProjectSet(fpgMap);
            } else {
                schedulingDao.updateFpgProjectSet(fpgMap);
            }

            //下置峰平谷数据
            Map<String,Object> param=new HashMap<>();
            param.put("project_id",sjfFpgProjectSet.getProjectId());
            param.put("is_all",1);
            List<Map<String, Object>> houseList = schedulingDao.getMeterDataByProject(param); //所有分户
            String value=getFpgSetVal(peak,plain,valley);
            for(int h=0;h<houseList.size();h++){
                String lpBoxSN=String.valueOf(houseList.get(h).get("sn"));
                String lpName=houseList.get(h).get("device_name")+"_"+constantConfig.getSjfTimeTag();
                commonService.webServiceSetVal(lpName,lpBoxSN,value);  //下置
            }
        }
        logger.info(today + "更新峰平谷设置成功!");
    }

    public String getFpgSetType(String timeType) {
        //14:00-17:00,19:00-22:00
        StringBuffer result = new StringBuffer("");
        String[] splits = timeType.split(",");
        for (int i = 0; i < splits.length; i++) {
            String split = splits[i];
            int begin = Integer.parseInt(split.split("-")[0].split(":")[0]);
            int end = Integer.parseInt(split.split("-")[1].split(":")[0]);
            if (end == 0) {
                end = 24;
            }
            for (int j = begin; j < end; j++) {
                result.append(j + ",");
            }
        }
        if (result.toString().substring(result.toString().length() - 1).equals(",")) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    public String getFpgSetVal(String peak,String plain,String valley) {
        //14:00-17:00,19:00-22:00
        List<Map<String,String>> list=new ArrayList<>();
        Map<String,String> map=new HashMap<>();
        String[] splits = peak.split(","); //峰
        for (int i = 0; i < splits.length; i++) {
            String split = splits[i];
            String key1 = split.split("-")[0].split(":")[0]+split.split("-")[0].split(":")[1];
            String key2 = split.split("-")[1].split(":")[0]+split.split("-")[1].split(":")[1];
            if (key2.equals("0000")) {
                key2 = "2400";
            }
            map=new HashMap<>();
            map.put("key1",key1);
            map.put("key2",key2);
            map.put("key3","2");  //峰
            list.add(map);
        }

        splits =plain.split(","); //平
        for (int i = 0; i < splits.length; i++) {
            String split = splits[i];
            String key1 = split.split("-")[0].split(":")[0]+split.split("-")[0].split(":")[1];
            String key2 = split.split("-")[1].split(":")[0]+split.split("-")[1].split(":")[1];
            if (key2.equals("0000")) {
                key2 = "2400";
            }
            map=new HashMap<>();
            map.put("key1",key1);
            map.put("key2",key2);
            map.put("key3","3");  //平
            list.add(map);
        }


        splits =valley.split(","); //谷
        for (int i = 0; i < splits.length; i++) {
            String split = splits[i];
            String key1 = split.split("-")[0].split(":")[0]+split.split("-")[0].split(":")[1];
            String key2 = split.split("-")[1].split(":")[0]+split.split("-")[1].split(":")[1];
            if (key2.equals("0000")) {
                key2 = "2400";
            }
            map=new HashMap<>();
            map.put("key1",key1);
            map.put("key2",key2);
            map.put("key3","4");  //谷
            list.add(map);
        }

        //排序
        Collections.sort(list, new Comparator<Map>() {
            @Override
            public int compare(Map o1, Map o2) {
                try {
                    int key1 = Integer.parseInt(o1.get("key1").toString());
                    int key2 = Integer.parseInt(o2.get("key1").toString());
                    if (key1 > key2) {
                        return 1;
                    } else if (key1 < key2) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        StringBuffer result1 = new StringBuffer("");
        StringBuffer result2 = new StringBuffer("");
        StringBuffer result3 = new StringBuffer("");
        for(int i=0;i<list.size();i++){
            result1.append(list.get(i).get("key1")+",");
            result2.append(list.get(i).get("key2")+",");
            result3.append(list.get(i).get("key3")+",");
        }

        if (result1.toString().substring(result1.toString().length() - 1).equals(",")) {
            result1.deleteCharAt(result1.length() - 1);
        }
        if (result2.toString().substring(result2.toString().length() - 1).equals(",")) {
            result2.deleteCharAt(result2.length() - 1);
        }
        if (result3.toString().substring(result3.toString().length() - 1).equals(",")) {
            result3.deleteCharAt(result3.length() - 1);
        }

        return result1.append(":").append(result2).append(":").append(result3).toString();

    }


    /**
     * 插入消费情况 ( 定时器 每小时插入)
     * @param
     * @return
     */
    public void sjfInsertDayData(String project_id){
        commonService.sjfInsertDayData(project_id);
    }


    /**
     *  收缴费APP 每小时执行  欠费用户跳闸
     */
    public  void sjfTrippingOperation(String project_id) {
        System.out.println("定时检测欠费用户"+new Date());
        //所有项目 遍历项目是因为满足去不同的MongoDB库取数据
        Map<String,Object> projectMap=new HashMap<>();
        projectMap.put("project_id",project_id);
        List<Map<String, Object>> projectList = commonDao.getSjfAllProject(projectMap);
        String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
        List<Map<String, Object>> resultlist = new ArrayList<>(); //所有欠费数据
        Map<String, Object> resultMap = new HashMap<>();//一条欠费数据
        for (int j = 0; j < projectList.size(); j++) { //遍历项目
            //所有用户
            List<Map<String, Object>> houseList = commonDao.getSjfMeterDataByProject(projectList.get(j));
            //存入余额
            houseList=commonService.addBalanceToHouseList(houseList,projectList.get(j));

            for (int i = 0; i < houseList.size(); i++) {
                Map<String,Object> house=houseList.get(i);
                Double balance=Double.parseDouble(String.valueOf(house.get("balance")));
                if (balance > 0) {
                    continue;
                }
                resultMap = new HashMap<>();
                resultMap.put("house_id", house.get("house_id"));
                resultMap.put("house_name", house.get("house_name"));
                resultMap.put("device_name", house.get("device_name"));
                resultMap.put("sn", house.get("sn"));
                resultMap.put("balance", balance);
                resultlist.add(resultMap);
            }
        }
        //所有欠费用户 跳闸
        for (int i = 0; i < resultlist.size(); i++) {
            String lpName=String.valueOf(resultlist.get(i).get("device_name"))+"_"+constantConfig.getSjfOpeningTag();
            String lpBoxSN=String.valueOf(resultlist.get(i).get("sn"));
            commonService.webServiceSetVal(lpName,lpBoxSN,"1");//分闸
        }
    }


    /**
     * APP  每天凌晨更新任务 火灾 巡检任务管理
     */
    public  void insertFirePatrolLog(String project_id) {
        Map<String,Object> map=new HashMap<>();
        map.put("project_id",project_id);
        List<Map<String, Object>> list= schedulingDao.getAllFirePatrolList(map);  //所有集合
        String date=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD); //当天日期 年月日
        String week=dateUtil.getDayWeekOfDate1(new Date());  //当天周几
        String monthDate=DateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYYMM); //当月
        int year=Integer.parseInt(monthDate.substring(0,4));
        int month=Integer.parseInt(monthDate.substring(4,6));
        String firstDate=dateUtil.getFirstDayOfMonth(year,month);  //当月第一天
        int type=0;
        int patrol_id=0;
        Map<String, Object> log=new HashMap<>();
        List<Map<String, Object>> logList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(dateUtil.isEffectiveDate(date,String.valueOf(list.get(i).get("begin_time")),String.valueOf(list.get(i).get("end_time")),DateUtil.DATE_FORMAT_YYYY_MM_DD)){
                type=Integer.parseInt(String.valueOf(list.get(i).get("patrol_type")));  //分类
                patrol_id=Integer.parseInt(String.valueOf(list.get(i).get("patrol_id")));  //分类
                if(type==1){ //日
                    log=new JSONObject();
                    log.put("patrol_id",patrol_id);
                    log.put("patrol_date",date+" "+week);
                    logList.add(log);
                }
                if(type==2){ //周
                    if("星期一".equals(week)){ //星期一
                        log=new JSONObject();
                        log.put("patrol_id",patrol_id);
                        log.put("patrol_date",date+" "+week);
                        logList.add(log);
                    }
                }
                if(type==3){ //月
                    if(date.equals(firstDate)){ //當月第一天
                        log=new JSONObject();
                        log.put("patrol_id",patrol_id);
                        log.put("patrol_date",date+" "+week);
                        logList.add(log);
                    }
                }
            }
        }
        map=new HashMap<>();
        map.put("list",logList);
        schedulingDao.addAllFirePatrolLog(logList);
    }


}
