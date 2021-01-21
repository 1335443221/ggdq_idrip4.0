package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sl.common.config.AccountDictionary;
import com.sl.common.utils.PageUtil;
import com.sl.common.utils.WebResult;
import com.sl.idripweb.dao.WebAccountTrainDao;
import com.sl.idripweb.dao.WebAccountUserDao;
import com.sl.idripweb.service.WebAccountTrainService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WebAccountTrainServiceImpl implements WebAccountTrainService {

    @Autowired
    private WebAccountTrainDao trainDao;
    @Autowired
    private WebAccountUserDao userDao;
    @Autowired
    private AccountDictionary accountDictionary;

    //##########################培训管理############################
    //获取培训台账列表数据
    @Override
    public WebResult getTrainList(Map<String, Object> map) {
        //处理筛选条件
        handelDate(map);

        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int count = trainDao.getTrainListCount(map);
        //获取列表
        List<Map<String, Object>> trainList = trainDao.getTrainList(map);
        //遍历数据给每条数据添加材料信息
        trainList.forEach(each -> {
            String id = String.valueOf(each.get("id"));
            map.put("id", id);
            List<Map<String, Object>> trainMaterial = trainDao.getTrainMaterial(map);
            each.put("material", trainMaterial);
        });
        resultMap.put("recordList", trainList);
        resultMap.put("count", count);
        return WebResult.success(resultMap);
    }

    //通过id获取培训台账
    @Override
    public WebResult getTrainById(Map<String, Object> map) {
        Map<String, Object> trainById = trainDao.getTrainById(map);
        //添加资料信息
        List<Map<String, Object>> trainMaterial = trainDao.getTrainMaterial(map);
        trainById.put("material", trainMaterial);
        return WebResult.success(trainById);
    }

    //新增培训台账
    @Transactional
    @Override
    public WebResult addTrain(Map<String, Object> map) {
        if(map.get("materials") != null) map.put("has_material", 1);
        else map.put("has_material", 2);
        //新增培训台账基础
        int flag = 0;
        flag += trainDao.addTrain(map);
        //新增培训材料
        flag += addTrainMaterial(map, flag);
        //新增培训台账培训材料
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //编辑培训台账
    @Transactional
    @Override
    public WebResult updateTrain(Map<String, Object> map) {
        if(map.get("materials") != null) map.put("has_material", 1);
        else map.put("has_material", 2);
        //修改培训台账基础
        int flag = 0;
        flag += trainDao.updateTrain(map);
        //删除某培训台账id下所有的培训材料
        flag += trainDao.deleteTrainMaterial(map);
        //新增培训材料
        flag += addTrainMaterial(map, flag);
        //新增培训台账培训材料
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //删除培训台账
    @Transactional
    @Override
    public WebResult deleteTrain(Map<String, Object> map) {
        map.put("ids", String.valueOf(map.get("ids")).split(","));
        int flag = 0;
        //批量删除培训台账
        flag += trainDao.deleteTrains(map);
        //批量删除培训台账id下所有的培训材料
        flag += trainDao.deleteTrainMaterials(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //获取所有培训内容
    @Override
    public WebResult getTrainContents(Map<String, Object> map) {
        return WebResult.success(trainDao.getTrainContents(map));
    }

    //添加培训材料
    private int addTrainMaterial(Map<String, Object> map, int flag) {
        //培训材料转为数组
        if (map.get("materials") != null) {
            String materials = String.valueOf(map.get("materials"));
            map.put("materials", JSONArray.parseArray(materials));
        }
        flag += trainDao.addTrainMaterial(map);
        return flag;
    }



    //##########################培训单############################
    //获取培训单列表数据
    @Override
    public WebResult getTrainSheetList(Map<String, Object> map) {
        //处理筛选条件
        handelDate(map);
        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int count = trainDao.getTrainSheetListCount(map);
        //获取列表
        List<Map<String, Object>> trainSheetList = trainDao.getTrainSheetList(map);
        //遍历数据给每条数据添加培训文件
        trainSheetList.forEach(each -> {
            String id = String.valueOf(each.get("id"));
            map.put("id", id);
            List<Map<String, Object>> trainSheetFile = trainDao.getTrainSheetFile(map);
            each.put("files", trainSheetFile);
        });
        resultMap.put("recordList", trainSheetList);
        resultMap.put("count", count);
        return WebResult.success(resultMap);
    }

    //通过id获取单个培训单
    @Override
    public WebResult getTrainSheetById(Map<String, Object> map) {
        Map<String, Object> trainSheetById = trainDao.getTrainSheetById(map);
        //添加培训文件
        List<Map<String, Object>> trainSheetFile = trainDao.getTrainSheetFile(map);
        trainSheetById.put("files", trainSheetFile);
        return WebResult.success(trainSheetById);
    }

    //新增培训单
    @Transactional
    @Override
    public WebResult addTrainSheet(Map<String, Object> map) {
        //新增培训单
        int flag = 0;
        flag += trainDao.addTrainSheet(map);
        //新增培训单文件
        flag += addTrainSheetFile(map, flag);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //编辑培训单
    @Transactional
    @Override
    public WebResult updateTrainSheet(Map<String, Object> map) {
        //修改培训单
        int flag = 0;
        flag += trainDao.updateTrainSheet(map);
        //删除某培训单id下所有的培训文件
        flag += trainDao.deleteTrainSheetFile(map);
        //新增培训文件
        flag += addTrainSheetFile(map, flag);
        //新增培训文件
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //删除培训单
    @Transactional
    @Override
    public WebResult deleteTrainSheet(Map<String, Object> map) {
        int flag = 0;
        //删除培训单
        flag += trainDao.deleteTrainSheet(map);
        //删除培训单文件
        flag += trainDao.deleteTrainSheetFile(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //添加培训文件
    private int addTrainSheetFile(Map<String, Object> map, int flag) {
        //培训材料转为数组
        if (map.get("files") != null) {
            String files = String.valueOf(map.get("files"));
            map.put("files", JSONArray.parseArray(files));
        }
        flag += trainDao.addTrainSheetFile(map);
        return flag;
    }




    //##########################数据统计############################
    //数据统计页面大接口
    @Override
    public WebResult statistics(Map<String, Object> map) {
        //结果集
        Map<String, Object> result = new HashMap<>();

        //参与培训男女比例
        Map<String, Object> sexRatio = getSexRatio(map);
        //参与培训部门比例
        Map<String, Object> departmentRatio = getDepartmentRatio(map);
        //培训成绩
        Map<String, Object> trainsResult = getTrainsResult(map);
        //各部门参与人数
        Map<String, Object> departmentMonth = getDepartmentMonth(map);

        result.put("sexRatio", sexRatio);
        result.put("departmentRatio", departmentRatio);
        result.put("trainsResult", trainsResult);
        result.put("departmentMonth", departmentMonth);
        return WebResult.success(result);
    }

    //参与培训男女比例
    private Map<String, Object> getSexRatio(Map<String, Object> map) {
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> sexRatioChart = trainDao.getSexRatioChart(map);
        String legendStr = "男,女";
        List<String> legendData = Arrays.asList(legendStr.split(","));
        //没有数据的补零
        zeroComplementPie(sexRatioChart, legendData);
        result.put("legendData", legendData);
        result.put("seriesData", sexRatioChart);
        return result;
    }

    //参与培训部门比例
    private Map<String, Object> getDepartmentRatio(Map<String, Object> map) {
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> outlookRatio = trainDao.getDepartmentRatioChart(map);
        //获取所有部门
        List<Map<String, Object>> allDepartment = userDao.getAllDepartment(map);
        List<String> legendData = allDepartment.stream().map(each -> String.valueOf(each.get("label"))).collect(Collectors.toList());
        //没有数据的补零
        zeroComplementPie(outlookRatio, legendData);
        result.put("legendData", legendData);
        result.put("seriesData", outlookRatio);
        return result;
    }

    //培训成绩
    private Map<String, Object> getTrainsResult(Map<String, Object> map) {
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> userType = trainDao.getTrainsResultChart(map);
        List<String> xAxisData = accountDictionary.getTrainAchievement();
        //没有数据的补零
        List<Integer> seriesData = zeroComplementBar(userType, xAxisData);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        return result;
    }

    //各部门参与人数
    private Map<String, Object> getDepartmentMonth(Map<String, Object> map) {
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        //获取所有部门
        List<Map<String, Object>> allDepartment = userDao.getAllDepartment(map);
        List<String> legendData = allDepartment.stream().map(each -> String.valueOf(each.get("label"))).collect(Collectors.toList());
        String xAxisStr = "一月,二月,三月,四月,五月,六月,七月,八月,九月,十月,十一月,十二月";
        List<String> xAxisData = Arrays.asList(xAxisStr.split(","));
        List<Map<String, Object>> departmentMonthChart = trainDao.getDepartmentMonthChart(map);
        Map<String, List<Map<String, Object>>> monthData = new HashMap<>();
        Map<String, List<Integer>> seriesData = new HashMap<>();
        departmentMonthChart.forEach(each -> {
            String dname = String.valueOf(each.get("dname"));
            each.put("name", each.get("date"));
            each.remove("date");
            legendData.forEach(month -> {
                if(monthData.get(month) == null) monthData.put(month, new ArrayList<>());
                if(month.equals(dname)){
                    monthData.get(month).add(each);
                }else{
                    Map<String, Object> dData = new HashMap<>();
                    dData.put("name", each.get("date"));
                    dData.put("value", 0);
                    dData.put("dname", month);
                    monthData.get(month).add(dData);
                }
            });
        });
        Set<Map.Entry<String, List<Map<String, Object>>>> entries = monthData.entrySet();
        Iterator<Map.Entry<String, List<Map<String, Object>>>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<Map<String, Object>>> next = iterator.next();
            String key = next.getKey();
            List<Map<String, Object>> value = next.getValue();
            //没有数据的补零
            seriesData.put(key, zeroComplementBar(value, xAxisData));
        }
        result.put("legendData", legendData);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        return result;
    }

    //数据补零--适用于饼图数据
    private void zeroComplementPie(List<Map<String, Object>> sexRatioChart, List<String> legendData) {
        //从list集合中，取出字段name的列表
        List<String> names = sexRatioChart.stream().map(each -> String.valueOf(each.get("name"))).collect(Collectors.toList());
        legendData.forEach(each -> {
            if(!names.contains(each)){
                Map<String, Object> zeroMap = new HashMap<>();
                zeroMap.put("name", each);
                zeroMap.put("value", 0);
                sexRatioChart.add(zeroMap);
            }
        });
    }

    //数据补零--适用于折线图柱形图数据
    private List<Integer> zeroComplementBar(List<Map<String, Object>> sexRatioChart, List<String> xAxisData) {
        //从list集合中，取出字段name的列表
        List<Integer> datas = new ArrayList<>();
        Map<String, Integer> valueMap = new HashMap<>();
        sexRatioChart.forEach(each -> {
            valueMap.put(String.valueOf(each.get("name")), Integer.parseInt(String.valueOf(each.get("value"))));
        });
        xAxisData.forEach(each -> {
            if(valueMap.get(each) == null)
                datas.add(0);
            else
                datas.add(valueMap.get(each));
        });
        return datas;
    }




    //处理筛选条件
    private void handelDate(Map<String, Object> map) {
        //筛选条件开始时间
        if (map.get("begin_date") == null || StringUtils.isEmpty(String.valueOf(map.get("begin_date")))) {
            map.put("begin_date", "2000-01-01");
        }

        //筛选条件结束时间
        if (map.get("end_date") == null || StringUtils.isEmpty(String.valueOf(map.get("end_date")))) {
            map.put("end_date", "2099-01-01");
        }
    }
}
