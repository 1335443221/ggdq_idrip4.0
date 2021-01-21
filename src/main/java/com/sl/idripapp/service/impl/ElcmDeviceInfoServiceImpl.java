package com.sl.idripapp.service.impl;

import com.google.gson.Gson;
import com.sl.common.utils.AppResult;
import com.sl.common.utils.RedisPoolUtil;
import com.sl.idripapp.dao.ElcmDeviceDao;
import com.sl.idripapp.entity.ElcmDeviceTypeTree;
import com.sl.idripapp.service.ElcmDeviceInfoService;
import com.sl.common.utils.JwtToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service("deviceInfoService")
public class ElcmDeviceInfoServiceImpl implements ElcmDeviceInfoService {

    @Autowired
    private ElcmDeviceDao deviceInfoDao;
    @Autowired
    private RedisPoolUtil redisPoolUtil;


    //获取所有的设备类型树
    @Override
    public AppResult getDeviceTypeTree(Map<String, Object> map) {
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        ArrayList<ElcmDeviceTypeTree> typeList=deviceInfoDao.getDeviceTypeTree(map);
        //设备数量
        ArrayList<Map<String,Object>> deviceCountList=deviceInfoDao.getDeviceCountByType(map);
        Map<Integer, Integer> deviceCount=new HashMap<>();
        for (Map<String,Object> item:deviceCountList){
            deviceCount.put(Integer.parseInt(item.get("device_type_id").toString()),Integer.parseInt(item.get("deviceCount").toString()));
        }
        ElcmDeviceTypeTree elcmDeviceTypeTree=new ElcmDeviceTypeTree();
        List<ElcmDeviceTypeTree> result=elcmDeviceTypeTree.getElcmDeviceTypeTreeByRecursion(0,typeList,deviceCount);

        return AppResult.success(result);
    }


    //获取所有的设备类型
    @Override
    public AppResult getDeviceTypes(Map<String, Object> map) {
        map.remove("token");
        List<HashMap<String, Object>> result= deviceInfoDao.getDeviceTypes();
        return AppResult.success(result);
    }

    //获取所有的设备状态
    @Override
    public AppResult getDeviceStatus(Map<String, Object> map) {
        map.remove("token");
        List<HashMap<String, Object>> result= deviceInfoDao.getDeviceStatus();
        return AppResult.success(result);
    }

    //根据设备类型和设备状态获取设备列表
    @Override
    public AppResult getDeviceList(Map<String, Object> map) {
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        Integer fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        Map<String, Object> result = new HashMap<>();
        Integer pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("pageSize", pageSize);
        int total = deviceInfoDao.getDeviceListCount(map);
        if((fromNum+pageSize) >= total){
            result.put("is_LastPage",true);
        }else{
            result.put("is_LastPage",false);
        }
        ArrayList<HashMap<String, Object>> deviceList = deviceInfoDao.getDeviceList(map);
        for(int i=0;i<deviceList.size();i++){
            if(deviceList.get(i).get("device_url") == null || StringUtils.isEmpty(String.valueOf(deviceList.get(i).get("device_url")))){
                deviceList.get(i).put("device_url", new ArrayList<>());
            }else{
                deviceList.get(i).put("device_url", Arrays.asList(String.valueOf(deviceList.get(i).get("device_url")).split(",")));
            }
        }
        result.put("devices_data", deviceList);
        return AppResult.success(result);
    }

    //根据设备id获取单个设备
    @Override
    public AppResult getDeviceById(Map<String, Object> map) {
        String deviceId = (String)map.get("device_id");
        if(StringUtils.isEmpty(deviceId)) return AppResult.error("1003");
        map.remove("token");
        HashMap<String, Object> deviceById = setDeviceUrl(map);
        return AppResult.success(deviceById);
    }

    private HashMap<String, Object> setDeviceUrl(Map<String, Object> map) {
        HashMap<String, Object> deviceById = deviceInfoDao.getDeviceById(map);
        if(deviceById.get("device_url") == null || StringUtils.isEmpty(String.valueOf(deviceById.get("device_url")))){
            deviceById.put("device_url", new ArrayList<>());
        }else{
            deviceById.put("device_url", Arrays.asList(String.valueOf(deviceById.get("device_url")).split(",")));
        }
        return deviceById;
    }

    //根据设备id获取技术参数
    @Override
    public AppResult getTechnologyParam(Map<String, Object> map) {
        String deviceId = (String)map.get("device_id");
        if(StringUtils.isEmpty(deviceId)) return AppResult.error("1003");
        map.remove("token");
        ArrayList<HashMap<String, Object>> technologyParam = deviceInfoDao.getTechnologyParam(map);
        Map<String, ArrayList<HashMap<String, Object>>> resultMap = new HashMap<>();
        for(HashMap<String, Object> param : technologyParam){
            String groupName = String.valueOf(param.get("group_name"));
            if(resultMap.get(groupName) == null)
                resultMap.put(groupName, new ArrayList<>());
            param.remove("group_name");
            resultMap.get(groupName).add(param);
        }
        ArrayList<HashMap<String, Object>> result = new ArrayList<>();
        //遍历map封装为集合
        for(String key : resultMap.keySet()){
            HashMap<String, Object> each = new HashMap<>();
            each.put("groupName", key);
            each.put("params", resultMap.get(key));
            result.add(each);
        }
        return AppResult.success(result);
    }

    //根据设备id获取设备保养清单
    @Override
    public AppResult getMaintenanceList(Map<String, Object> map) {
        String deviceId = (String)map.get("device_id");
        if(StringUtils.isEmpty(deviceId)) return AppResult.error("1003");
        map.remove("token");
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        Integer fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);
        Map<String, Object> result = new HashMap<>();
        HashMap<String, Object> device = setDeviceUrl(map);
        Integer device_id = Integer.parseInt(String.valueOf(device.get("device_id")));
        String device_name = String.valueOf(device.get("device_name"));
        String device_number = String.valueOf(device.get("device_number"));
        Integer pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("pageSize", pageSize);
        int total = deviceInfoDao.getMaintenanceListCount(map);
        if((fromNum+pageSize) >= total){
            result.put("is_LastPage",true);
        }else{
            result.put("is_LastPage",false);
        }
        result.put("device_id", device_id);
        result.put("device_name", device_name);
        result.put("device_number", device_number);
        result.put("maintain_data", deviceInfoDao.getMaintenanceList(map));
        return AppResult.success(result);
    }

    //根据设备id获取设备维保记录
    @Override
    public AppResult getMaintenanceRecord(Map<String, Object> map) {
        String deviceId = (String)map.get("device_id");
        if(StringUtils.isEmpty(deviceId)) return AppResult.error("1003");
        map.remove("token");
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        Integer fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);
        Map<String, Object> result = new HashMap<>();
        Integer pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("pageSize", pageSize);
        int total = deviceInfoDao.getMaintenanceRecordCount(map);
        if((fromNum+pageSize) >= total){
            result.put("is_LastPage",true);
        }else{
            result.put("is_LastPage",false);
        }
        ArrayList<HashMap<String, Object>> maintenanceRecord = deviceInfoDao.getMaintenanceRecord(map);
        for(int i=0;i<maintenanceRecord.size();i++){
            if(maintenanceRecord.get(i).get("complete_url") == null || StringUtils.isEmpty(String.valueOf(maintenanceRecord.get(i).get("complete_url")))){
                maintenanceRecord.get(i).put("complete_url", new ArrayList<>());
            }else{
                maintenanceRecord.get(i).put("complete_url", Arrays.asList(String.valueOf(maintenanceRecord.get(i).get("complete_url")).split(",")));
            }
        }
        //维保记录中增加每种维保类型数量统计
        ArrayList<HashMap<String, Object>> maintenanceTypeCount = deviceInfoDao.getMaintenanceTypeCount(map);
        Map<Integer, Integer> countMap = new HashMap<>();
        for(HashMap<String, Object> each : maintenanceTypeCount){
            Integer maintenance_type_id = Integer.parseInt(String.valueOf(each.get("maintenance_type_id")));
            Integer count = Integer.parseInt(String.valueOf(each.get("count")));
            countMap.put(maintenance_type_id, count);
        }
        for(int i=1;i<5;i++){
            if(countMap.get(i) == null){
                HashMap<String, Object> eachMap = new HashMap<>();
                eachMap.put("maintenance_type_id", i);
                switch (i){
                    case 1 :
                        eachMap.put("maintenance_type_name", "保养");
                        break;
                    case 2 :
                        eachMap.put("maintenance_type_name", "点检");
                        break;
                    case 3 :
                        eachMap.put("maintenance_type_name", "维修");
                        break;
                    case 4 :
                        eachMap.put("maintenance_type_name", "故障");
                        break;
                    default:
                        eachMap.put("maintenance_type_name", "保养");
                }
                eachMap.put("count", 0);
                maintenanceTypeCount.add(eachMap);
            }
        }
        result.put("record_data", maintenanceRecord);
        result.put("maintenance_type_count", maintenanceTypeCount);
        return AppResult.success(result);
    }



    //根据设备id获取实时数据
    @Override
    public AppResult getRealTimeData(Map<String, Object> map) {
        String deviceId = (String) map.get("device_id");
        if (StringUtils.isEmpty(deviceId)) return AppResult.error("1003");
        map.remove("token");
        Map<String, Object> device=deviceInfoDao.getRedisDevice(map);  //设备
        List<Map<String, Object>> tagList=deviceInfoDao.getRedisDeviceTag(device);  //标签
        Map<String, Object> dataMap = new HashMap<>();
        try {
            if (tagList == null || tagList.size() <= 0){
                return AppResult.success(dataMap);
            }
            DecimalFormat df = new DecimalFormat("0"); //保留整数
            Gson gson = new Gson();
            Set<byte[]> keySet = new HashSet<byte[]>();
            for (int m = 0; m < tagList.size(); m++) {
                String _key = String.valueOf(device.get("tg_id")) +":"+ String.valueOf(device.get("tg_device_name"))+":" + String.valueOf(tagList.get(m).get("tag_name"));
                keySet.add(_key.getBytes());
            }
            if (keySet.size() < 1){
                return AppResult.success(dataMap);
            }
            byte[][] values = redisPoolUtil.mget(keySet);
            byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
            HashMap<String, Object> data = new HashMap<String, Object>();
            for (int i = 0; i < keySet.size(); ++i) {
                if (values[i] == null) continue;
                HashMap<String, Object> fileMap = new HashMap<>();
                String val = new String(values[i], "utf-8");
                fileMap.put("val", df.format(Double.parseDouble(gson.fromJson(val, HashMap.class).get("val").toString())));
                String key = new String(keys[i], "utf-8");
                int index=key.indexOf(":");
                index=key.indexOf(":", index+1);
                data.put(key.substring(index+1), fileMap);
            }

            for (int m = 0; m < tagList.size(); m++) {
                if(data.get(tagList.get(m).get("tag_name"))!=null){
                    HashMap<String, Object> fileMap =(HashMap<String, Object>)data.get(tagList.get(m).get("tag_name"));
                    tagList.get(m).put("val",fileMap.get("val"));
                }else{
                    tagList.get(m).put("val","——");
                }
            }
        } catch (Exception e) {
        }

        String ua="——";
        String ub="——";
        String uc="——";
        String ia="——";
        String ib="——";
        String ic="——";
        for (int m = 0; m < tagList.size(); m++) {
            if(String.valueOf(tagList.get(m).get("tag_name")).equals("ua")){
                ua=String.valueOf(tagList.get(m).get("val"));
                tagList.remove(m);
                m--;
            }else if(String.valueOf(tagList.get(m).get("tag_name")).equals("ub")){
                ub=String.valueOf(tagList.get(m).get("val"));
                tagList.remove(m);
                m--;
            }else if(String.valueOf(tagList.get(m).get("tag_name")).equals("uc")){
                uc=String.valueOf(tagList.get(m).get("val"));
                tagList.remove(m);
                m--;
            }else if(String.valueOf(tagList.get(m).get("tag_name")).equals("ia")){
                ia=String.valueOf(tagList.get(m).get("val"));
                tagList.remove(m);
                m--;
            }else if(String.valueOf(tagList.get(m).get("tag_name")).equals("ib")){
                ib=String.valueOf(tagList.get(m).get("val"));
                tagList.remove(m);
                m--;
            }else if(String.valueOf(tagList.get(m).get("tag_name")).equals("ic")){
                ic=String.valueOf(tagList.get(m).get("val"));
                tagList.remove(m);
                m--;
            }

        }
        List<Map<String,Object>> result=new ArrayList<>();
        if(!ua.equals("——")||!ub.equals("——")||!uc.equals("——")){
            Map<String,Object> uMap=new HashMap<>();
            uMap.put("tag_name","Ua/Ub/Uc（V)");
            uMap.put("val",ua+"/"+ub+"/"+uc);
            result.add(uMap);
        }
        if(!ia.equals("——")||!ib.equals("——")||!ic.equals("——")){
            Map<String,Object> iMap=new HashMap<>();
            iMap.put("tag_name","Ia/Ib/Ic（A)");
            iMap.put("val",ia+"/"+ib+"/"+ic);
            result.add(iMap);
        }
        for (int m = 0; m < tagList.size(); m++) {
            Map<String,Object> tagMap=new HashMap<>();
            tagMap.put("tag_name",tagList.get(m).get("tag_name")+" ("+tagList.get(m).get("unit")+")");
            tagMap.put("val",tagList.get(m).get("val"));
            result.add(tagMap);
        }



        //获取redis ep实时数据
        dataMap.put("status_name",device.get("status_name"));  //状态
        dataMap.put("loop",device.get("loop")); //回路
        dataMap.put("values",result);
        return AppResult.success(dataMap);

    }

    //根据设备id获取元器件组成
    @Override
    public AppResult getComponent(Map<String, Object> map) {
        String deviceId = (String)map.get("device_id");
        if(StringUtils.isEmpty(deviceId)) return AppResult.error("1003");
        map.remove("token");
        ArrayList<HashMap<String, Object>> components = deviceInfoDao.getComponent(map);
        Map<String, ArrayList<HashMap<String, Object>>> resultMap = new HashMap<>();
        for(HashMap<String, Object> component : components){
            String groupName = String.valueOf(component.get("group_name"));
            if(resultMap.get(groupName) == null)
                resultMap.put(groupName, new ArrayList<>());
            component.remove("group_name");
            resultMap.get(groupName).add(component);
        }
        ArrayList<HashMap<String, Object>> result = new ArrayList<>();
        //遍历map封装为集合
        for(String key : resultMap.keySet()){
            HashMap<String, Object> each = new HashMap<>();
            each.put("groupName", key);
            ArrayList<HashMap<String, Object>> hashMaps = resultMap.get(key);
            for(int i=0;i<hashMaps.size();i++){
                if(hashMaps.get(i).get("component_url") == null || StringUtils.isEmpty(String.valueOf(hashMaps.get(i).get("component_url")))){
                    hashMaps.get(i).put("component_url", new ArrayList<>());
                }else{
                    hashMaps.get(i).put("component_url", Arrays.asList(String.valueOf(hashMaps.get(i).get("component_url")).split(",")));
                }
            }
            each.put("components", resultMap.get(key));
            each.put("count", resultMap.get(key).size());
            result.add(each);
        }
        return AppResult.success(result);
    }

    //获取设备资料
    @Override
    public AppResult getMaterial(Map<String, Object> map) {
        String deviceId = (String)map.get("device_id");
        if(StringUtils.isEmpty(deviceId)) return AppResult.error("1003");
        map.remove("token");
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        Integer fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);
        Map<String, Object> result = new HashMap<>();
        Integer pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("pageSize", pageSize);
        int total = deviceInfoDao.getMaterialCount(map);
        if((fromNum+pageSize) >= total){
            result.put("is_LastPage",true);
        }else{
            result.put("is_LastPage",false);
        }
        ArrayList<HashMap<String, Object>> material = deviceInfoDao.getMaterial(map);
        result.put("record_data", material);
        return AppResult.success(result);
    }
}
