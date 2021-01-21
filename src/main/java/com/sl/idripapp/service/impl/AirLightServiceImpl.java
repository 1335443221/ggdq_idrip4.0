package com.sl.idripapp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.sl.common.config.ConstantConfig;
import com.sl.common.entity.params.SetValParams;
import com.sl.common.service.CommonService;
import com.sl.common.utils.*;
import com.sl.idripapp.dao.AirLightDao;
import com.sl.idripapp.service.AirLightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: AirLightServiceImpl
 * Description: 空调照明APP
 */
@Service("airLightDataImpl")
public class AirLightServiceImpl implements AirLightService {
    @Autowired
    private AirLightDao airLightDao;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    ConstantConfig constant;
    @Autowired
    CommonService commonService;
    @Autowired
    private RedisPoolUtil redisPoolUtil;

    /**
     * 空调-新增定时设置
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public AppResult addAirTimingSetting(Map<String, Object> map) {
        map.put("create_by", JwtToken.getUserName(String.valueOf(map.get("token"))));
        map.put("create_user_id", JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        //空调列表id
        String[] id_list = String.valueOf(map.get("air_id_list")).split(",");
        //时间列表
        List<Map> TimingList = JSONArray.parseArray(String.valueOf(map.get("date")), Map.class);
        //空调定时设置表
        Map<String, Object> tsMap = new HashMap<>();
        //脚本定时设置表
        Map<String, Object> icMap = new HashMap<>();
        //关联关系表
        Map<String, Object> relationMap = new HashMap<>();
        //当前日期所在年
        String Year = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY);
        int result = 0;
        for (int i = 0; i < id_list.length; i++) {
            for (int j = 0; j < TimingList.size(); j++) {
                //空调定时设置表
                tsMap = new HashMap<>();
                tsMap.put("start_month", TimingList.get(j).get("start_month"));
                tsMap.put("start_day", TimingList.get(j).get("start_day"));
                tsMap.put("start_time", TimingList.get(j).get("start_time"));
                tsMap.put("end_month", TimingList.get(j).get("end_month"));
                tsMap.put("end_day", TimingList.get(j).get("end_day"));
                tsMap.put("end_time", TimingList.get(j).get("end_time"));
                tsMap.put("ac_id", id_list[i]);
                tsMap.put("create_by", map.get("create_by"));
                result = airLightDao.addAirTimingSetting(tsMap);

                //脚本定时设置表
                icMap = new HashMap<>();
                icMap.put("ac_id", id_list[i]);
                icMap.put("ac_tag", airLightDao.getAirSwitchTagById(id_list[i]));
                icMap.put("bdate", Year + "-" + TimingList.get(j).get("start_month") + "-" + TimingList.get(j).get("start_day"));
                icMap.put("edate", Year + "-" + TimingList.get(j).get("end_month") + "-" + TimingList.get(j).get("end_day"));
                icMap.put("btime", TimingList.get(j).get("start_time"));
                icMap.put("etime", TimingList.get(j).get("end_time"));
                icMap.put("mode", "0,1,2,3,4,5,6");
                icMap.put("create_user_id", map.get("create_user_id"));
                airLightDao.addAirIntelligentControl(icMap);
                //关联关系表
                relationMap = new HashMap<>();
                relationMap.put("ts_id", tsMap.get("ts_id"));
                relationMap.put("ic_id", icMap.get("ic_id"));
                airLightDao.addAirRelation(relationMap);
            }
        }
        if (result == 0) {
            return AppResult.error("1010");
        } else {
            return AppResult.success();
        }
    }


    /**
     * 空调-删除某个定时设置
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public AppResult deleteAirTimingSetting(Map<String, Object> map) {
        map.remove("token");
        airLightDao.deleteAirIntelligentControl(map);
        int result = airLightDao.deleteAirTimingSetting(map);
        if (result == 0) {
            return AppResult.error("1010");
        } else {
            return AppResult.success();
        }
    }


    /**
     * 空调-修改某个定时设置
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public AppResult updateAirTimingSetting(Map<String, Object> map) {
        map.remove("token");
        Map<String, Object> icMap = new HashMap<>();
        String Year = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY);
        icMap.put("bdate", Year + "-" + map.get("start_month") + "-" + map.get("start_day"));
        icMap.put("edate", Year + "-" + map.get("end_month") + "-" + map.get("end_day"));
        icMap.put("btime", map.get("start_time"));
        icMap.put("etime", map.get("end_time"));
        icMap.put("ts_id", map.get("ts_id"));
        icMap.put("begin_time", Year + "-01-01");
        airLightDao.updateAirIntelligentControl(icMap);
        int result = airLightDao.updateAirTimingSetting(map);
        if (result == 0) {
            return AppResult.error("1010");
        } else {
            return AppResult.success();
        }
    }


    /**
     * 空调-下置
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public AppResult airSetVal(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("operator", JwtToken.getUserName(String.valueOf(map.get("token"))));
        map.remove("token");

        //下置单个设备
        ArrayList<Map<String, Object>> airList = airLightDao.getAirList(map);

        Map<String, Object> recordMap = new HashMap<>();
        recordMap.put("project_id", map.get("project_id"));
        recordMap.put("operator", map.get("operator"));
        if (String.valueOf(map.get("val")).equals("1")) {
            recordMap.put("operation", "开");
        } else {
            recordMap.put("operation", "关");
        }
        SetValParams setValParams = new SetValParams();
        String result="";
        if (map.get("air_id") != null) {
            setValParams.setProjectId(Integer.parseInt(airList.get(0).get("project_id").toString()));
            setValParams.setFactoryId(Integer.parseInt(airList.get(0).get("factory_id").toString()));
            setValParams.setTgName(airList.get(0).get("tg_name").toString());
            setValParams.setTag(airList.get(0).get("tagName").toString());
            setValParams.setVal(map.get("val").toString());
            result=commonService.setVal(setValParams);

            recordMap.put("building_id", airList.get(0).get("building_id"));
            recordMap.put("air_id", airList.get(0).get("air_id"));
            recordMap.put("device_desc", airList.get(0).get("device_desc"));
            airLightDao.insertAirOperationRecord(recordMap);
            //下置多个设备
        } else if (map.get("building_id") != null) {
            for (int i = 0; i < airList.size(); i++) {
                setValParams = new SetValParams();
                setValParams.setProjectId(Integer.parseInt(airList.get(i).get("project_id").toString()));
                setValParams.setFactoryId(Integer.parseInt(airList.get(i).get("factory_id").toString()));
                setValParams.setTgName(airList.get(i).get("tg_name").toString());
                setValParams.setTag(airList.get(i).get("tagName").toString());
                setValParams.setVal(map.get("val").toString());
                result= commonService.setVal(setValParams);
            }
            recordMap.put("building_id", map.get("building_id"));
            recordMap.put("device_desc", airLightDao.getAirBuilding(map).get(0).get("building_name"));
             airLightDao.insertAirOperationRecord(recordMap);
        }

        return AppResult.success(result);
    }

    /**
     * 空调-获取定时设置信息
     *
     * @param
     * @return
     */
    @Override
    public AppResult getAirTimingSetting(Map<String, Object> map) {
        map.remove("token");
        List<Map<String, Object>> result = airLightDao.getAirTimingSetting(map);
        return AppResult.success(result);
    }


    /**
     * 空调-获取操作记录
     *
     * @param
     * @return
     */
    @Override
    public AppResult getAirOperationRecord(Map<String, Object> map) {
        if (map.get("pageSize") == null || map.get("pageSize") == "" || map.get("pageNum") == "" || map.get("pageNum") == null) {
            map.put("pageNum", 1);
            map.put("pageSize", 20);
        }
        int fromNum = (Integer.parseInt(String.valueOf(map.get("pageNum"))) - 1) * Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");

        Map<String, Object> result = new HashMap<>();
        int pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        int total = airLightDao.getAirOperationRecordCount(map);
        if ((fromNum + pageSize) >= total) {
            result.put("is_lastpage", true);
        } else {
            result.put("is_lastpage", false);
        }
        result.put("recordList", airLightDao.getAirOperationRecord(map));
        return AppResult.success(result);
    }


    /**
     * 空调-获取楼的信息
     *
     * @param
     * @return
     */
    @Override
    public AppResult getAirBuilding(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");

        ArrayList<Map<String, Object>> buildingList = airLightDao.getAirBuilding(map);
        for (int i = 0; i < buildingList.size(); i++) {
            buildingList.get(i).put("device_list", airLightDao.getAirByParams(buildingList.get(i)));
        }
        return AppResult.success(buildingList);
    }


    /**
     * 空调-获取空调列表
     *
     * @param
     * @return
     */
    @Override
    public AppResult getAirList(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");

        ArrayList<Map<String, Object>> devices = airLightDao.getAirList(map);
        ArrayList<Map<String, Object>> tagMap = airLightDao.getAirSwitchTag(map);
        Map<String, Object> dataMap = new HashMap<>();
        try {
            if (tagMap == null || tagMap.size() <= 0) {
                return AppResult.success(devices);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            ArrayList<Map<String, Object>> devicesResult = new ArrayList<Map<String, Object>>();
            Gson gson = new Gson();
            Set<byte[]> keySet = new HashSet<byte[]>();
            for (int d = 0; d < devices.size(); d++) {
                Map<String, Object> device = devices.get(d);
                if (device == null || device.get("tagName") == null) {
                    continue;
                }
                for (int m = 0; m < tagMap.size(); m++) {
                    String _key = String.valueOf(device.get("tagName")) + ":" + String.valueOf(tagMap.get(m).get("tag_name"));
                    keySet.add(_key.getBytes());
                }

            }
            if (keySet.size() < 1) {
                return AppResult.success(devices);
            }
            byte[][] values = redisPoolUtil.mget(keySet);
            byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
            // key-value对
            for (int d = 0; d < devices.size(); d++) {
                Map<String, Object> device = devices.get(d);
                String _key = String.valueOf(device.get("tagName")) + ":" + String.valueOf(device.get("switch_tag"));
                if (keys.length < 1) {
                    continue;
                }
                for (int i = 0; i < keySet.size(); ++i) {
                    if (values[i] == null) {
                        continue;
                    }
                    String key = new String(keys[i], "utf-8");
                    if (key.equals(_key)) {
                        String val = new String(values[i], "utf-8");
                        String state = df.format(Double.parseDouble(gson.fromJson(val, dataMap.getClass()).get("val").toString()));
                        if (state.contains("1")) {
                            devices.get(d).put("state", 1);
                        }
                    }

                }
            }
        } catch (Exception e) {
        }
        return AppResult.success(devices);
    }


    /**
     * 空调-能耗
     *
     * @param
     * @return
     */
    @Override
    public AppResult getAirEnergy(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        if (map.get("type") == null) { //没填周还是月的时候  默认为周
            map.put("type", 1);
        }

        Map<String, Object> resultMap = new HashMap<>();
        String[] id_list = String.valueOf(map.get("device_id")).split(",");
        map.put("id_list", id_list);
        ArrayList<Map<String, Object>> devices = airLightDao.getAirByParams(map);
        String[] meter_id_list = new String[devices.size()];
        for (int i = 0; i < devices.size(); i++) {
            meter_id_list[i] = String.valueOf(devices.get(i).get("elec_meter_id"));
        }

        String type = String.valueOf(map.get("type"));
        String newDate = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD); //今天
        String firstDayOfWeek = DateUtil.parseDateToStr(dateUtil.getFirstDayOfWeek(new Date()), DateUtil.DATE_FORMAT_YYYY_MM_DD);//这周一
        String firstDayOfLastWeek = dateUtil.getSomeDayOfTime(firstDayOfWeek, DateUtil.DATE_FORMAT_YYYY_MM_DD, -7);  //上周一
        String lastDayOfLastWeek = dateUtil.getSomeDayOfTime(firstDayOfWeek, DateUtil.DATE_FORMAT_YYYY_MM_DD, -1);  //上周日
        String firstDayofNewMonth = dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, 0); //当前月的第一天
        String firstDayofLastDay = dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, -1); //前一个月的第一天
        String lastMonthLastDay = dateUtil.getLastMonthLastDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, 0); //前月的最后一天

        List<String> thisWeek = dateUtil.getDays(firstDayOfWeek, newDate); //当前周日期集合
        List<String> lastWeek = dateUtil.getDays(firstDayOfLastWeek, lastDayOfLastWeek); //前一周日期集合
        List<String> thisMonth = dateUtil.getDays(firstDayofNewMonth, newDate); //当前月日期集合
        List<String> lastMonth = dateUtil.getDays(firstDayofLastDay, lastMonthLastDay); //前一月日期集合

        Map<String, Object> params = new HashMap<>();
        params.put("end_time", newDate);
        params.put("start_time", firstDayofLastDay);
        params.put("array", meter_id_list);
        if (type.equals("1")) {  //周能耗
            ArrayList<Map<Object, Object>> this_energy = new ArrayList<>();
            ArrayList<Map<Object, Object>> last_energy = new ArrayList<>();
            Map<Object, Object> energyMap = new HashMap<>();
            Map<String, Object> dateMap = parseDateListToMap(airLightDao.getAirEnergyList(params));
            for (int j = 0; j < thisWeek.size(); j++) {  //当前周
                energyMap = new HashMap<>();
                energyMap.put("day", j + 1);
                energyMap.put("ep", dateMap.get(thisWeek.get(j)) == null ? 0 : dateMap.get(thisWeek.get(j)));
                this_energy.add(energyMap);
            }

            for (int j = 0; j < lastWeek.size(); j++) { //上一周
                energyMap = new HashMap<>();
                energyMap.put("day", j + 1);
                energyMap.put("ep", dateMap.get(lastWeek.get(j)) == null ? 0 : dateMap.get(lastWeek.get(j)));
                last_energy.add(energyMap);
            }
            resultMap.put("this_energy", this_energy);
            resultMap.put("last_energy", last_energy);
        } else {  //月能耗
            ArrayList<Map<Object, Object>> this_energy = new ArrayList<>();
            ArrayList<Map<Object, Object>> last_energy = new ArrayList<>();
            Map<Object, Object> energyMap = new HashMap<>();
            Map<String, Object> dateMap = parseDateListToMap(airLightDao.getAirEnergyList(params));

            for (int j = 0; j < thisMonth.size(); j++) {  //当前月
                energyMap = new HashMap<>();
                energyMap.put("day", j + 1);
                energyMap.put("ep", dateMap.get(thisMonth.get(j)) == null ? 0 : dateMap.get(thisMonth.get(j)));
                this_energy.add(energyMap);
            }

            for (int j = 0; j < lastMonth.size(); j++) { //上一月
                energyMap = new HashMap<>();
                energyMap.put("day", j + 1);
                energyMap.put("ep", dateMap.get(lastMonth.get(j)) == null ? 0 : dateMap.get(lastMonth.get(j)));
                last_energy.add(energyMap);
            }
            resultMap.put("this_energy", this_energy);
            resultMap.put("last_energy", last_energy);
        }
        return AppResult.success(resultMap);
    }

    //获取日期Map
    public Map<String, Object> parseDateListToMap(List<Map<String, Object>> list) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map.put(String.valueOf(list.get(i).get("date")), list.get(i).get("power"));
        }
        return map;
    }


    //--------------------------------Light。。-----------------------------///
    //--------------------------------Light。。-----------------------------///
    //--------------------------------Light。。-----------------------------///

    /**
     * 照明-新增定时设置
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public AppResult addLightTimingSetting(Map<String, Object> map) {
        map.put("create_by", JwtToken.getUserName(String.valueOf(map.get("token"))));
        map.put("create_user_id", JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");

        String[] id_list = String.valueOf(map.get("light_id_list")).split(",");
        List<Map> TimingList = JSONArray.parseArray(String.valueOf(map.get("date")), Map.class);
        Map<String, Object> tsMap = new HashMap<>();
        Map<String, Object> icMap = new HashMap<>();
        Map<String, Object> relationMap = new HashMap<>();
        //当前日期所在年
        String Year = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY);
        int result = 0;
        for (int i = 0; i < id_list.length; i++) {
            for (int j = 0; j < TimingList.size(); j++) {
                tsMap = new HashMap<>();
                tsMap.put("start_month", TimingList.get(j).get("start_month"));
                tsMap.put("start_day", TimingList.get(j).get("start_day"));
                tsMap.put("start_time", TimingList.get(j).get("start_time"));
                tsMap.put("end_month", TimingList.get(j).get("end_month"));
                tsMap.put("end_day", TimingList.get(j).get("end_day"));
                tsMap.put("end_time", TimingList.get(j).get("end_time"));
                tsMap.put("light_id", id_list[i]);
                tsMap.put("create_by", map.get("create_by"));

                result = airLightDao.addLightTimingSetting(tsMap);

                //脚本定时设置表
                icMap = new HashMap<>();
                icMap.put("light_id", id_list[i]);
                icMap.put("light_tag", airLightDao.getLightSwitchTagById(id_list[i]));
                icMap.put("bdate", Year + "-" + TimingList.get(j).get("start_month") + "-" + TimingList.get(j).get("start_day"));
                icMap.put("edate", Year + "-" + TimingList.get(j).get("end_month") + "-" + TimingList.get(j).get("end_day"));
                icMap.put("btime", TimingList.get(j).get("start_time"));
                icMap.put("etime", TimingList.get(j).get("end_time"));
                icMap.put("create_user_id", map.get("create_user_id"));
                icMap.put("enable", 1);
                airLightDao.addLightIntelligentControl(icMap);
                //关联关系表
                relationMap = new HashMap<>();
                relationMap.put("ts_id", tsMap.get("ts_id"));
                relationMap.put("ic_id", icMap.get("ic_id"));
                airLightDao.addLightRelation(relationMap);
            }
        }
        if (result == 0) {
            return AppResult.error("1010");
        } else {
            return AppResult.success();
        }
    }


    /**
     * 照明-删除某个定时设置
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public AppResult deleteLightTimingSetting(Map<String, Object> map) {
        map.remove("token");
        airLightDao.deleteLightTimingSetting(map);
        int result = airLightDao.deleteLightTimingSetting(map);
        if (result == 0) {
            return AppResult.error("1010");
        } else {
            return AppResult.success();
        }
    }


    /**
     * 照明-修改某个定时设置
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public AppResult updateLightTimingSetting(Map<String, Object> map) {
        map.remove("token");
        Map<String, Object> icMap = new HashMap<>();
        String Year = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY);

        icMap.put("bdate", Year + "-" + map.get("start_month") + "-" + map.get("start_day"));
        icMap.put("edate", Year + "-" + map.get("end_month") + "-" + map.get("end_day"));
        icMap.put("btime", map.get("start_time"));
        icMap.put("etime", map.get("end_time"));
        icMap.put("ts_id", map.get("ts_id"));
        icMap.put("begin_time", Year + "-01-01");
        airLightDao.updateLightIntelligentControl(icMap);

        int result = airLightDao.updateLightTimingSetting(map);
        if (result == 0) {
            return AppResult.error("1010");
        } else {
            return AppResult.success();
        }
    }


    /**
     * 照明-获取定时设置
     *
     * @param
     * @return
     */
    @Override
    public AppResult getLightTimingSetting(Map<String, Object> map) {
        map.remove("token");
        List<Map<String, Object>> result = airLightDao.getLightTimingSetting(map);
        return AppResult.success(result);
    }


    /**
     * 照明-获取操作记录
     *
     * @param
     * @return
     */
    @Override
    public AppResult getLightOperationRecord(Map<String, Object> map) {
        if (map.get("pageSize") == null || map.get("pageSize") == "" || map.get("pageNum") == "" || map.get("pageNum") == null) {
            map.put("pageNum", 1);
            map.put("pageSize", 20);
        }
        int fromNum = (Integer.parseInt(String.valueOf(map.get("pageNum"))) - 1) * Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");

        Map<String, Object> result = new HashMap<>();
        int pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        int total = airLightDao.getLightOperationRecordCount(map);
        if ((fromNum + pageSize) >= total) {
            result.put("is_lastpage", true);
        } else {
            result.put("is_lastpage", false);
        }
        result.put("recordList", airLightDao.getLightOperationRecord(map));
        return AppResult.success(result);
    }


    /**
     * 照明-获取楼的信息
     *
     * @param map
     * @return
     */
    @Override
    public AppResult getLightBuilding(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");

        ArrayList<Map<String, Object>> buildingList = airLightDao.getLightBuilding(map);
        String building_id = "";
        for (int i = 0; i < buildingList.size(); i++) {
            buildingList.get(i).put("device_list", airLightDao.getLightByParams(buildingList.get(i)));
        }
        return AppResult.success(buildingList);
    }

    /**
     * 照明-获取照明列表
     *
     * @param map
     * @return
     */
    @Override
    public AppResult getLightList(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");

        ArrayList<Map<String, Object>> devices = airLightDao.getLightList(map);
        ArrayList<Map<String, Object>> tagMap = airLightDao.getLightSwitchTag(map);
        Map<String, Object> dataMap = new HashMap<>();
        try {
            if (tagMap == null || tagMap.size() <= 0) {
                return AppResult.success(devices);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            ArrayList<Map<String, Object>> devicesResult = new ArrayList<Map<String, Object>>();
            Gson gson = new Gson();
            Set<byte[]> keySet = new HashSet<byte[]>();
            for (int d = 0; d < devices.size(); d++) {
                Map<String, Object> device = devices.get(d);
                if (device == null || device.get("tagName") == null) continue;
                for (int m = 0; m < tagMap.size(); m++) {
                    String _key = String.valueOf(device.get("tagName")) + ":" + String.valueOf(tagMap.get(m).get("tag_name"));
                    keySet.add(_key.getBytes());
                }

            }
            if (keySet.size() < 1) {
                return AppResult.success(devices);
            }
            byte[][] values = redisPoolUtil.mget(keySet);
            byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
            // key-value对
            for (int d = 0; d < devices.size(); d++) {
                Map<String, Object> device = devices.get(d);
                String _key = String.valueOf(device.get("tagName")) + ":" + String.valueOf(device.get("switch_tag"));
                if (keys.length < 1) {
                    continue;
                }
                for (int i = 0; i < keySet.size(); ++i) {
                    if (values[i] == null) {
                        continue;
                    }
                    String key = new String(keys[i], "utf-8");
                    if (key.equals(_key)) {
                        String val = new String(values[i], "utf-8");
                        String state = df.format(Double.parseDouble(gson.fromJson(val, dataMap.getClass()).get("val").toString()));
                        if (state.contains("1")) {
                            devices.get(d).put("state", 1);
                        }
                    }

                }
            }
        } catch (Exception e) {
        }
        return AppResult.success(devices);
    }

    /**
     * 照明-能耗
     *
     * @param
     * @return
     */
    @Override
    public AppResult getLightEnergy(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        if (map.get("type") == null) { //没填周还是月的时候  默认为周
            map.put("type", 1);
        }

        Map<String, Object> resultMap = new HashMap<>();
        String[] id_list = String.valueOf(map.get("device_id")).split(",");
        map.put("id_list", id_list);
        ArrayList<Map<String, Object>> devices = airLightDao.getLightByParams(map);
        String[] meter_id_list = new String[devices.size()];
        for (int i = 0; i < devices.size(); i++) {
            meter_id_list[i] = String.valueOf(devices.get(i).get("elec_meter_id"));
        }

        String type = String.valueOf(map.get("type"));
        String newDate = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD); //今天
        String firstDayOfWeek = DateUtil.parseDateToStr(dateUtil.getFirstDayOfWeek(new Date()), DateUtil.DATE_FORMAT_YYYY_MM_DD);//这周一
        String firstDayOfLastWeek = dateUtil.getSomeDayOfTime(firstDayOfWeek, DateUtil.DATE_FORMAT_YYYY_MM_DD, -7);  //上周一
        String lastDayOfLastWeek = dateUtil.getSomeDayOfTime(firstDayOfWeek, DateUtil.DATE_FORMAT_YYYY_MM_DD, -1);  //上周日
        String firstDayofNewMonth = dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, 0); //当前月的第一天
        String firstDayofLastDay = dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, -1); //前一个月的第一天
        String lastMonthLastDay = dateUtil.getLastMonthLastDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, 0); //前月的最后一天

        List<String> thisWeek = dateUtil.getDays(firstDayOfWeek, newDate); //当前周日期集合
        List<String> lastWeek = dateUtil.getDays(firstDayOfLastWeek, lastDayOfLastWeek); //前一周日期集合
        List<String> thisMonth = dateUtil.getDays(firstDayofNewMonth, newDate); //当前月日期集合
        List<String> lastMonth = dateUtil.getDays(firstDayofLastDay, lastMonthLastDay); //前一月日期集合

        Map<String, Object> params = new HashMap<>();
        params.put("end_time", newDate);
        params.put("start_time", firstDayofLastDay);
        params.put("array", meter_id_list);
        if (type.equals("1")) {  //周能耗
            ArrayList<Map<Object, Object>> this_energy = new ArrayList<>();
            ArrayList<Map<Object, Object>> last_energy = new ArrayList<>();
            Map<Object, Object> energyMap = new HashMap<>();
            Map<String, Object> dateMap = parseDateListToMap(airLightDao.getLightEnergyList(params));

            for (int j = 0; j < thisWeek.size(); j++) {  //当前周
                energyMap = new HashMap<>();
                energyMap.put("day", j + 1);
                energyMap.put("ep", dateMap.get(thisWeek.get(j)) == null ? 0 : dateMap.get(thisWeek.get(j)));
                this_energy.add(energyMap);
            }

            for (int j = 0; j < lastWeek.size(); j++) { //上一周
                energyMap = new HashMap<>();
                energyMap.put("day", j + 1);
                energyMap.put("ep", dateMap.get(lastWeek.get(j)) == null ? 0 : dateMap.get(lastWeek.get(j)));
                last_energy.add(energyMap);
            }
            resultMap.put("this_energy", this_energy);
            resultMap.put("last_energy", last_energy);
        } else {  //月能耗
            ArrayList<Map<Object, Object>> this_energy = new ArrayList<>();
            ArrayList<Map<Object, Object>> last_energy = new ArrayList<>();
            Map<Object, Object> energyMap = new HashMap<>();
            Map<String, Object> dateMap = parseDateListToMap(airLightDao.getLightEnergyList(params));

            for (int j = 0; j < thisMonth.size(); j++) {  //当前月
                energyMap = new HashMap<>();
                energyMap.put("day", j + 1);
                energyMap.put("ep", dateMap.get(thisMonth.get(j)) == null ? 0 : dateMap.get(thisMonth.get(j)));
                this_energy.add(energyMap);
            }

            for (int j = 0; j < lastMonth.size(); j++) { //上一月
                energyMap = new HashMap<>();
                energyMap.put("day", j + 1);
                energyMap.put("ep", dateMap.get(lastMonth.get(j)) == null ? 0 : dateMap.get(lastMonth.get(j)));
                last_energy.add(energyMap);
            }
            resultMap.put("this_energy", this_energy);
            resultMap.put("last_energy", last_energy);
        }
        return AppResult.success(resultMap);
    }

    /**
     * 照明-下置
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public AppResult lightSetVal(Map<String, Object> map) {
        //下置单个设备
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        ArrayList<Map<String, Object>> LightList = airLightDao.getLightList(map);
        Map<String, Object> recordMap = new HashMap<>();
        recordMap.put("project_id", map.get("project_id"));
        recordMap.put("operator", map.get("operator"));
        if (String.valueOf(map.get("val")).equals("1")) {
            recordMap.put("operation", "开");
        } else {
            recordMap.put("operation", "关");
        }

        SetValParams setValParams = new SetValParams();
        String result="";
        if (map.get("light_id") != null) {
            setValParams.setProjectId(Integer.parseInt(LightList.get(0).get("project_id").toString()));
            setValParams.setFactoryId(Integer.parseInt(LightList.get(0).get("factory_id").toString()));
            setValParams.setTgName(LightList.get(0).get("tg_name").toString());
            setValParams.setTag(LightList.get(0).get("tagName").toString());
            setValParams.setVal(map.get("val").toString());
            result=commonService.setVal(setValParams);

            recordMap.put("building_id", LightList.get(0).get("building_id"));
            recordMap.put("Light_id", LightList.get(0).get("Light_id"));
            recordMap.put("device_desc", LightList.get(0).get("device_desc"));
            airLightDao.insertLightOperationRecord(recordMap);
            //下置多个设备
        } else if (map.get("building_id") != null) {
            for (int i = 0; i < LightList.size(); i++) {
                setValParams = new SetValParams();
                setValParams.setProjectId(Integer.parseInt(LightList.get(i).get("project_id").toString()));
                setValParams.setFactoryId(Integer.parseInt(LightList.get(i).get("factory_id").toString()));
                setValParams.setTgName(LightList.get(i).get("tg_name").toString());
                setValParams.setTag(LightList.get(i).get("tagName").toString());
                setValParams.setVal(map.get("val").toString());
                result= commonService.setVal(setValParams);
            }
            recordMap.put("building_id", map.get("building_id"));
            recordMap.put("device_desc", airLightDao.getLightBuilding(map).get(0).get("building_name"));
            airLightDao.insertLightOperationRecord(recordMap);
        }

        return AppResult.success(result);
    }

}
