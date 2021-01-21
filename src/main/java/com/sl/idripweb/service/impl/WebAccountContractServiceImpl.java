package com.sl.idripweb.service.impl;

import com.sl.common.config.AccountDictionary;
import com.sl.common.utils.DateUtil;
import com.sl.common.utils.PageUtil;
import com.sl.common.utils.WebResult;
import com.sl.idripweb.dao.WebAccountContractDao;
import com.sl.idripweb.service.WebAccountContractService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class WebAccountContractServiceImpl implements WebAccountContractService {

    @Autowired
    private WebAccountContractDao contractDao;

    //##########################合同台账############################
    //获取合同台账列表数据
    @Override
    public WebResult getContractList(Map<String, Object> map) {
        //处理筛选条件
        handelDate(map);

        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int count = contractDao.getContractListCount(map);
        //获取列表
        List<Map<String, Object>> contractList = contractDao.getContractList(map);
        resultMap.put("recordList", contractList);
        resultMap.put("count", count);
        return WebResult.success(resultMap);
    }

    //供应商台账合作项目列表
    @Override
    public WebResult getContractListSupplier(Map<String, Object> map) {
        List<Map<String, Object>> contractListSupplier = contractDao.getContractListSupplier(map);
        return WebResult.success(contractListSupplier);
    }

    //通过id获取合同台账
    @Override
    public WebResult getContractById(Map<String, Object> map) {
        Map<String, Object> contractById = contractDao.getContractById(map);
        return WebResult.success(contractById);
    }

    //新增合同台账
    @Transactional
    @Override
    public WebResult addContract(Map<String, Object> map) {
        int flag = contractDao.addContract(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //编辑合同台账
    @Transactional
    @Override
    public WebResult updateContract(Map<String, Object> map) {
        //先查询合同台账
        Map<String, Object> contractById = contractDao.getContractById(map);
        Date terminationDate1 = DateUtil.parseStrToDate(String.valueOf(map.get("termination_date")), "yyyy-MM-dd");
        Date terminationDate = DateUtil.parseStrToDate(String.valueOf(contractById.get("termination_date")).substring(0, 11), "yyyy-MM-dd");
        if(terminationDate1.getTime() > terminationDate.getTime()){
            map.put("update_time", new Date());
        }
        int flag = contractDao.updateContract(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //删除合同台账
    @Transactional
    @Override
    public WebResult deleteContract(Map<String, Object> map) {
        map.put("ids", String.valueOf(map.get("ids")).split(","));
        int flag = contractDao.deleteContract(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //通过合同id获取提醒事项
    @Override
    public WebResult getRemindersByContractId(Map<String, Object> map) {
        Map<String, Object> contractById = contractDao.getRemindersByContractId(map);
        return WebResult.success(contractById);
    }

    //新增合同提醒事项
    @Transactional
    @Override
    public WebResult addContractReminders(Map<String, Object> map) {
        int flag = contractDao.addContractReminders(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //编辑合同提醒事项
    @Transactional
    @Override
    public WebResult updateContractReminders(Map<String, Object> map) {
        int flag = contractDao.updateContractReminders(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
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



    //##########################数据统计############################
    //数据统计页面大接口
    @Override
    public WebResult statistics(Map<String, Object> map) {
        //结果集
        Map<String, Object> result = new HashMap<>();

        //状态统计
        Map<String, Object> statusStatistics = getStatusStatistics(map);
        //延期/中止合同
        Map<String, Object> delayTermination = getDelayTermination(map);
        //签署/到期合同
        Map<String, Object> signExpire = getSignExpire(map);

        result.put("statusStatistics", statusStatistics);
        result.put("delayTermination", delayTermination);
        result.put("signExpire", signExpire);
        return WebResult.success(result);
    }

    //状态统计
    public Map<String, Object> getStatusStatistics(Map<String, Object> map){
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        String legendStr = "生效,终止";
        List<String> legendData = Arrays.asList(legendStr.split(","));
        List<Map<String, Object>> seriesData = new ArrayList<>();
        //获取生效合同数量
        map.put("status", "effect");
        int effectCount = contractDao.getStatusStatistics(map);
        Map<String, Object> effectMap = new HashMap<>();
        effectMap.put("name", legendData.get(0));
        effectMap.put("value", effectCount);
        seriesData.add(effectMap);
        //获取终止合同数量
        map.put("status", "termination");
        int terminationCount = contractDao.getStatusStatistics(map);
        Map<String, Object> terminationMap = new HashMap<>();
        terminationMap.put("name", legendData.get(1));
        terminationMap.put("value", terminationCount);
        seriesData.add(terminationMap);
        result.put("legendData", legendData);
        result.put("seriesData", seriesData);
        return result;
    }

    //延期/中止合同
    public Map<String, Object> getDelayTermination(Map<String, Object> map){
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        String legendStr = "延期,中止";
        List<String> legendData = Arrays.asList(legendStr.split(","));
        String xAxisStr = "一月,二月,三月,四月,五月,六月,七月,八月,九月,十月,十一月,十二月";
        List<String> xAxisData = Arrays.asList(xAxisStr.split(","));
        Map<String, List<Integer>> seriesData = new HashMap<>();
        seriesData.put("延期", new ArrayList<>());
        seriesData.put("中止", new ArrayList<>());
        //延期合同
        Map<String, Object> delayMap = new HashMap<>();
        map.put("type", "delay");
        List<Map<String, Object>> delay = contractDao.getDelayTermination(map);
        delay.forEach(each -> {
            delayMap.put(String.valueOf(each.get("date")), each.get("value"));
        });
        //中止合同
        Map<String, Object> terminationMap = new HashMap<>();
        map.put("type", "termination");
        List<Map<String, Object>> termination = contractDao.getDelayTermination(map);
        termination.forEach(each -> {
            terminationMap.put(String.valueOf(each.get("date")), each.get("value"));
        });
        //遍历所有月份补零处理
        xAxisData.forEach(each -> {
            if(delayMap.get(each) == null) seriesData.get("延期").add(0);
            else seriesData.get("延期").add(Integer.valueOf(String.valueOf(delayMap.get(each))));
            if(terminationMap.get(each) == null) seriesData.get("中止").add(0);
            else seriesData.get("中止").add(Integer.valueOf(String.valueOf(terminationMap.get(each))));
        });
        result.put("legendData", legendData);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        return result;
    }

    //签署/到期合同
    public Map<String, Object> getSignExpire(Map<String, Object> map){
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        String legendStr = "签署,到期";
        List<String> legendData = Arrays.asList(legendStr.split(","));
        String xAxisStr = "一月,二月,三月,四月,五月,六月,七月,八月,九月,十月,十一月,十二月";
        List<String> xAxisData = Arrays.asList(xAxisStr.split(","));
        Map<String, List<Integer>> seriesData = new HashMap<>();
        seriesData.put("签署", new ArrayList<>());
        seriesData.put("到期", new ArrayList<>());
        //签署合同
        Map<String, Object> signMap = new HashMap<>();
        map.put("type", "sign");
        List<Map<String, Object>> sign = contractDao.getSignExpire(map);
        sign.forEach(each -> {
            signMap.put(String.valueOf(each.get("date")), each.get("value"));
        });
        //到期合同
        Map<String, Object> expireMap = new HashMap<>();
        map.put("type", "expire");
        List<Map<String, Object>> expire = contractDao.getSignExpire(map);
        expire.forEach(each -> {
            expireMap.put(String.valueOf(each.get("date")), each.get("value"));
        });
        //遍历所有月份补零处理
        xAxisData.forEach(each -> {
            if(signMap.get(each) == null) seriesData.get("签署").add(0);
            else seriesData.get("签署").add(Integer.valueOf(String.valueOf(signMap.get(each))));
            if(expireMap.get(each) == null) seriesData.get("到期").add(0);
            else seriesData.get("到期").add(Integer.valueOf(String.valueOf(expireMap.get(each))));
        });
        result.put("legendData", legendData);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        return result;
    }
}
