package com.sl.idripapp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sl.common.config.ConstantConfig;
import com.sl.common.service.CommonService;
import com.sl.common.utils.AppResult;
import com.sl.common.utils.DateUtil;
import com.sl.common.utils.JwtToken;
import com.sl.idripapp.dao.ElecReportDao;
import com.sl.idripapp.service.ElecReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("elecMonitorImpl")
public class ElecReportServiceImpl implements ElecReportService {
    @Autowired
    ConstantConfig constant;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    ElecReportDao elecReportDao;
    @Autowired
    CommonService commonService;


    /**
     * 一个项目的所有厂区+配电室
     */
    @Override
    public AppResult getProjectFactorys(Map<String, Object> map) {
        //获取所有厂区列表
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        ArrayList<Map<String, Object>> factoryList= commonService.getFactoryByPid(map);
        for(int i=0;i<factoryList.size();i++){
            //获取该厂区下边所有区域分类 配电室的列表
            map.put("factory_id",factoryList.get(i).get("factory_id"));
            List<Map<String,Object>> categoryList= elecReportDao.getCategoryTransformerroom(map);
            //把区域分类列表 存入厂区中
            factoryList.get(i).put("category_list",categoryList);
        }
        return AppResult.success(factoryList);
    }


    /**
     * 单日分类列表
     */
    @Override
    public AppResult singleDayList(Map<String, Object> map) {
        if(map.get("pageSize")==null||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        int fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        int pageSize=Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);


        //如果厂区为空  则默认第一个厂区
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        ArrayList<Map<String, Object>> factoryList= commonService.getFactoryByPid(map);
        map.put("factory_id", factoryList.get(0).get("factory_id"));
        //如果配电室为空  则默认第一个配电室
        List<Map<String,Object>> categoryList= elecReportDao.getCategoryTransformerroom(map);
        map.put("category_id", categoryList.get(0).get("category_id"));
        //存入配电室id  方便查询分支列表
        map.put("transformerroom_id",categoryList.get(0).get("rid"));
        //如果时间为空 则默认前一天
        if(map.get("date")==null){
            map.put("date", DateUtil.parseDateToStr(DateUtil.addDate(new Date(),0,0,-1,0,0,0,0),DateUtil.DATE_FORMAT_YYYY_MM_DD));
        }

        //分支列表
        List<Map<String,Object>> branchList=elecReportDao.getBranchList(map);
        int total=elecReportDao.getBranchListCount(map);
        String meterIds="";
        for (int i=0;i<branchList.size();i++){
            if (i==branchList.size()-1){
                meterIds +=branchList.get(i).get("meter_id");
            }else{
                meterIds +=branchList.get(i).get("meter_id")+",";
            }
        }
        //获取能耗数据
        Map<String,Object> paramsData=new HashMap<>();
        paramsData.put("meterIds",meterIds);
        paramsData.put("date",map.get("date"));
        List<Map<String,Object>> singleDataList=elecReportDao.getSingleDayData(paramsData);
         Map<String,Object> singleData=new HashMap<>();
        for (int i=0;i<singleDataList.size();i++){
            singleData.put(singleDataList.get(i).get("meter_id").toString(),singleDataList.get(i).get("power"));
        }
        //把能耗数据插入分支列表
        List<Map<String,Object>> singleDayList=new ArrayList<>();
        for (int i=0;i<branchList.size();i++){
            Map<String, Object> singleDay=new HashMap<>();
            singleDay.put("branch_id",branchList.get(i).get("category_id"));
            singleDay.put("branch_name",branchList.get(i).get("category_name"));
            singleDay.put("consumption_value",singleData.get(branchList.get(i).get("meter_id").toString())==null?0:singleData.get(branchList.get(i).get("meter_id").toString()));
            singleDayList.add(singleDay);
        }
        //返回数据
        Map<String, Object> result=new HashMap<>();
        result.put("factory_id",factoryList.get(0).get("factory_id"));
        result.put("factory_name",factoryList.get(0).get("factory_name"));
        result.put("category_id",categoryList.get(0).get("category_id"));
        result.put("category_name",categoryList.get(0).get("category_name"));
        result.put("date",map.get("date"));
        if((fromNum+pageSize)>=total){
            result.put("is_lastpage",true);
        }else{
            result.put("is_lastpage",false);
        }
        result.put("singleDay_list",singleDayList);
    return AppResult.success(result);
    }


    /**
     * 区间分类列表
     */
    @Override
    public AppResult sectionList(Map<String, Object> map) {
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        int fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        int pageSize=Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);


        //如果厂区为空  则默认第一个厂区
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        ArrayList<Map<String, Object>> factoryList= commonService.getFactoryByPid(map);
        map.put("factory_id", factoryList.get(0).get("factory_id"));
        //如果配电室为空  则默认第一个配电室
        List<Map<String,Object>> categoryList= elecReportDao.getCategoryTransformerroom(map);
        map.put("category_id", categoryList.get(0).get("category_id"));
        //存入配电室id  方便查询分支列表
        map.put("transformerroom_id",categoryList.get(0).get("rid"));
        //如果时间为空 则默认一周的数据
        if(map.get("start_date")==null){
            map.put("start_date", DateUtil.parseDateToStr(DateUtil.addDate(new Date(),0,0,-8,0,0,0,0),DateUtil.DATE_FORMAT_YYYY_MM_DD));
            map.put("end_date", DateUtil.parseDateToStr(DateUtil.addDate(new Date(),0,0,-1,0,0,0,0),DateUtil.DATE_FORMAT_YYYY_MM_DD));
        }
        //分支列表
        List<Map<String,Object>> branchList=elecReportDao.getBranchList(map);
        int total=elecReportDao.getBranchListCount(map);
        String meterIds="";
        for (int i=0;i<branchList.size();i++){
            if (i==branchList.size()-1){
                meterIds +=branchList.get(i).get("meter_id");
            }else{
                meterIds +=branchList.get(i).get("meter_id")+",";
            }
        }

        //获取能耗数据
        Map<String,Object> paramsData=new HashMap<>();
        paramsData.put("meterIds",meterIds);
        paramsData.put("start_date",map.get("start_date"));
        paramsData.put("end_date",map.get("end_date"));
        List<Map<String,Object>> singleDataList=elecReportDao.getSectionData(paramsData);
        Map<String,Object> singleData=new HashMap<>();
        for (int i=0;i<singleDataList.size();i++){
            singleData.put(singleDataList.get(i).get("meter_id").toString(),singleDataList.get(i).get("power"));
        }
        //把能耗数据插入分支列表
        List<Map<String,Object>> singleDayList=new ArrayList<>();
        for (int i=0;i<branchList.size();i++){
            Map<String, Object> singleDay=new HashMap<>();
            singleDay.put("branch_id",branchList.get(i).get("category_id"));
            singleDay.put("branch_name",branchList.get(i).get("category_name"));
            singleDay.put("consumption_value",singleData.get(branchList.get(i).get("meter_id").toString())==null?0:singleData.get(branchList.get(i).get("meter_id").toString()));
            singleDayList.add(singleDay);
        }
        //返回数据
        LinkedHashMap<String, Object> result=new LinkedHashMap<>();
        result.put("factory_id",factoryList.get(0).get("factory_id"));
        result.put("factory_name",factoryList.get(0).get("factory_name"));
        result.put("category_id",categoryList.get(0).get("category_id"));
        result.put("category_name",categoryList.get(0).get("category_name"));
        result.put("start_date",map.get("start_date"));
        result.put("end_date",map.get("end_date"));
        if((fromNum+pageSize)>=total){
            result.put("is_lastpage",true);
        }else{
            result.put("is_lastpage",false);
        }
        result.put("section_list",singleDayList);
        return AppResult.success(result);
    }

    /**
     * 单日报表详情
     */
    @Override
    public AppResult singleDayDetail(Map<String, Object> map) {
        if(map.get("factory_id")==null||map.get("factory_id")==""||map.get("branch_id")==null||map.get("branch_id")==""||map.get("date")==null||map.get("date")==""){
            return AppResult.error("1003");
        }
        //分支数据
        map.put("category_id",map.get("branch_id"));
        Map<String,Object> branchData=elecReportDao.getCategoryByCategoryId(map);
        //获取能耗数据
        Map<String,Object> paramsData=new HashMap<>();
        paramsData.put("meterIds",branchData.get("meter_id"));
        paramsData.put("date",map.get("date"));
        List<Map<String,Object>> singleDayData=elecReportDao.getSingleDayData(paramsData);

        double power=0;
        List<Map<String,Object>> list=new ArrayList<>();
        if (singleDayData.size()>0){
            String power_per_hour=String.valueOf(singleDayData.get(0).get("power_per_hour")).replace("[", "").replace("]", "");
            List<String> hourList = Arrays.asList(power_per_hour.split(","));
            power=Double.parseDouble(String.valueOf(singleDayData.get(0).get("power")));
            for(int i=0;i<24;i++){
                Map<String,Object>  hourData= new HashMap<String, Object>();
                hourData.put("time",i+1);
                hourData.put("power",Double.parseDouble(hourList.get(i)));
                list.add(hourData);
            }
        }else{
            for(int i=0;i<24;i++){
                Map<String,Object>  hourData= new HashMap<String, Object>();
                hourData.put("time",i+1);
                hourData.put("power",0);
                list.add(hourData);
            }
        }

        Map<String, Object> result=new HashMap<>();
        result.put("branch_name", branchData.get("category_name"));
        result.put("branch_id", map.get("branch_id"));
        result.put("factory_id",map.get("factory_id"));
        result.put("date", map.get("date"));
        result.put("total_power",power);
        result.put("list", list);  //单日列表
        return AppResult.success(result);

    }


    /**
     * 区间报表详情
     * @param map
     * @return
     */
    @Override
    public AppResult sectionDetail(Map<String, Object> map) {
        if(map.get("factory_id")==null||map.get("factory_id")==""||map.get("branch_id")==null||map.get("branch_id")==""||map.get("start_date")==null||map.get("start_date")==""||map.get("end_date")==null||map.get("end_date")==""){
            return AppResult.success("1003");
        }
        //分支数据
        map.put("category_id",map.get("branch_id"));
        Map<String,Object> branchData=elecReportDao.getCategoryByCategoryId(map);
        //获取能耗数据
        Map<String,Object> paramsData=new HashMap<>();
        paramsData.put("meter_id",branchData.get("meter_id"));
        paramsData.put("start_date",map.get("start_date"));
        paramsData.put("end_date",map.get("end_date"));
        List<Map<String,Object>> sectionData=elecReportDao.getSectionDataDetail(paramsData);
        Map<String,Object> sectionMap=new HashMap<>();
        for (int i=0;i<sectionData.size();i++){
            sectionMap.put(String.valueOf(sectionData.get(i).get("date")),sectionData.get(i).get("power"));
        }
        //遍历日期集合
        Date startDate = DateUtil.parseStrToDate(String.valueOf(map.get("start_date")),DateUtil.DATE_FORMAT_YYYY_MM_DD);
        Date endDate = DateUtil.parseStrToDate(String.valueOf(map.get("end_date")),DateUtil.DATE_FORMAT_YYYY_MM_DD);
        List<String> dateList = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateList.add(sdf.format(startDate));
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(startDate);
        while (endDate.after(cStart.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cStart.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(sdf.format(cStart.getTime()));
        }
        //往日期中拼接数据
        double power=0;
        List<Map<String,Object>> list=new ArrayList<>();
        for(int i=0;i<dateList.size();i++){
            Map<String, Object> dataMap= new HashMap();
            dataMap.put("date", dateList.get(i));
            double dataPower=sectionMap.get(dateList.get(i))==null?0:Double.parseDouble(String.valueOf(sectionMap.get(dateList.get(i))));
            dataMap.put("power",dataPower);
            power+=dataPower;
            list.add(dataMap);
        }
        //返回数据
        Map<String, Object> result=new HashMap<>();
        result.put("branch_name", branchData.get("category_name"));
        result.put("branch_id",map.get("branch_id"));
        result.put("factory_id", map.get("factory_id"));
        result.put("start_date", map.get("start_date"));
        result.put("end_date", map.get("end_date"));
        result.put("list", list);  //区间列表
        result.put("total_power",power);
        return AppResult.success(result);
    }

    /**
     * 出线列表区间
     */
    @Override
    public AppResult sectionCoilinSort(Map<String, Object> map) {
        if(map.get("factory_id")==null||map.get("factory_id")==""||map.get("branch_id")==null||map.get("branch_id")==""||map.get("start_date")==null||map.get("start_date")==""||map.get("end_date")==null||map.get("end_date")==""){
            return AppResult.error("1003");
        }
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        int fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        int pageSize=Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);

        //进线数据
        map.put("category_id",map.get("branch_id"));
        Map<String,Object> categoryData=elecReportDao.getCategoryByCategoryId(map);
        //获取能耗数据
        Map<String,Object> paramsData=new HashMap<>();
        paramsData.put("meterIds",categoryData.get("meter_id"));
        paramsData.put("start_date",map.get("start_date"));
        paramsData.put("end_date",map.get("end_date"));
        List<Map<String,Object>> branchSectionData=elecReportDao.getSectionData(paramsData);
        double total_consumption_value=0;
        if (branchSectionData.size()>0&&branchSectionData.get(0).get("power")!=null){
            total_consumption_value=Double.parseDouble(String.valueOf(branchSectionData.get(0).get("power")));
        }

        //出线列表
        map.put("parent_category_id",map.get("branch_id"));
        List<Map<String,Object>> categoryList=elecReportDao.getCategoryByParentId(map);
        int total=elecReportDao.getCategoryByParentIdCount(map);
        String meterIds="";
        for (int i=0;i<categoryList.size();i++){
            if (i==categoryList.size()-1){
                meterIds +=categoryList.get(i).get("meter_id");
            }else{
                meterIds +=categoryList.get(i).get("meter_id")+",";
            }
        }
        //获取能耗数据
        paramsData.put("meterIds",meterIds);
        List<Map<String,Object>> sectionList=elecReportDao.getSectionData(paramsData);
        Map<String,Object> singleData=new HashMap<>();
        for (int i=0;i<sectionList.size();i++){
            singleData.put(sectionList.get(i).get("meter_id").toString(),sectionList.get(i).get("power"));
        }
        //把能耗数据插入分支列表
        List<Map<String,Object>> coilinSort=new ArrayList<>();
        for (int i=0;i<categoryList.size();i++){
            Map<String, Object> singleDay=new HashMap<>();
            singleDay.put("coilin_id",categoryList.get(i).get("category_id"));
            singleDay.put("coilin_name",categoryList.get(i).get("category_name"));
            singleDay.put("consumption_value",singleData.get(categoryList.get(i).get("meter_id").toString())==null?0:singleData.get(categoryList.get(i).get("meter_id").toString()));
            coilinSort.add(singleDay);
        }


        Map<String,Object> result=new HashMap<>();
        if((fromNum+pageSize)>=total){
            result.put("is_lastpage",true);
        }else{
            result.put("is_lastpage",false);
        }
        result.put("total_consumption_value", total_consumption_value);
        result.put("branch_id",map.get("branch_id"));
        result.put("factory_id",map.get("factory_id"));
        result.put("branch_name", categoryData.get("category_name"));
        result.put("coilin_sort", coilinSort);  //列表
        return AppResult.success(result);
    }

    /**
     * 出线列表单日
     */
    @Override
    public AppResult singleDayCoilinSort(Map<String, Object> map) {
        if(map.get("factory_id")==null||map.get("factory_id")==""||map.get("branch_id")==null||map.get("branch_id")==""||map.get("date")==null||map.get("date")==""){
            return AppResult.error("1003");
        }
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        int fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        int pageSize=Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);

        //进线数据
        map.put("category_id",map.get("branch_id"));
        Map<String,Object> categoryData=elecReportDao.getCategoryByCategoryId(map);
        //获取能耗数据
        Map<String,Object> paramsData=new HashMap<>();
        paramsData.put("meterIds",categoryData.get("meter_id"));
        paramsData.put("date",map.get("date"));
        List<Map<String,Object>> branchSingleDayData=elecReportDao.getSingleDayData(paramsData);
        double total_consumption_value=0;
        if (branchSingleDayData.size()>0&&branchSingleDayData.get(0).get("power")!=null){
            total_consumption_value=Double.parseDouble(String.valueOf(branchSingleDayData.get(0).get("power")));
        }

        //出线列表
        map.put("parent_category_id",map.get("branch_id"));
        List<Map<String,Object>> categoryList=elecReportDao.getCategoryByParentId(map);
        int total=elecReportDao.getCategoryByParentIdCount(map);
        String meterIds="";
        for (int i=0;i<categoryList.size();i++){
            if (i==categoryList.size()-1){
                meterIds +=categoryList.get(i).get("meter_id");
            }else{
                meterIds +=categoryList.get(i).get("meter_id")+",";
            }
        }
        //获取能耗数据
        paramsData.put("meterIds",meterIds);
        List<Map<String,Object>> singleDataList=elecReportDao.getSingleDayData(paramsData);
        Map<String,Object> singleData=new HashMap<>();
        for (int i=0;i<singleDataList.size();i++){
            singleData.put(singleDataList.get(i).get("meter_id").toString(),singleDataList.get(i).get("power"));
        }
        //把能耗数据插入分支列表
        List<Map<String,Object>> coilinSort=new ArrayList<>();
        for (int i=0;i<categoryList.size();i++){
            Map<String, Object> singleDay=new HashMap<>();
            singleDay.put("coilin_id",categoryList.get(i).get("category_id"));
            singleDay.put("coilin_name",categoryList.get(i).get("category_name"));
            singleDay.put("consumption_value",singleData.get(categoryList.get(i).get("meter_id").toString())==null?0:singleData.get(categoryList.get(i).get("meter_id").toString()));
            coilinSort.add(singleDay);
        }


        Map<String,Object> result=new HashMap<>();
        if((fromNum+pageSize)>=total){
            result.put("is_lastpage",true);
        }else{
            result.put("is_lastpage",false);
        }
        result.put("total_consumption_value", total_consumption_value);
        result.put("branch_id",map.get("branch_id"));
        result.put("factory_id",map.get("factory_id"));
        result.put("branch_name", categoryData.get("category_name"));
        result.put("coilin_sort", coilinSort);  //列表
        return AppResult.success(result);
    }

    //======================电流电压曲线==================//
    /**
     * 电流电压曲线
     * @param map
     * @return
     */
    @Override
    public AppResult getElecMonitorList(Map<String, Object> map) {
        if(map.get("factory_id")==null||map.get("factory_id")==""||map.get("branch_id")==null||map.get("branch_id")==""){
            return AppResult.error("1003");
        }
        //分支数据
        map.put("category_id",map.get("branch_id"));
        Map<String,Object> branchData=elecReportDao.getCategoryByCategoryId(map);
        String branch_name =String.valueOf(branchData.get("category_name"));;

        Map<String, Object> resultmap= new HashMap<String, Object>();
        resultmap.put("device_id",branchData.get("meter_id"));
        if(map.get("bdate")==null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            resultmap.put("bdate",sdf.format(new Date()));
        }else{
            resultmap.put("bdate",map.get("bdate"));
        }
        List<String> paramListUI=new ArrayList<>();
        paramListUI.add("uab");
        paramListUI.add("ubc");
        paramListUI.add("uca");
        paramListUI.add("ia");
        paramListUI.add("ib");
        paramListUI.add("ic");
        resultmap.put("list",paramListUI);
        List<Map<String, Object>> result=elecReportDao.getUIData(resultmap);
        List<Map<String, Object>> resultList=new ArrayList<>();
        Map<String, Object> uiMap=new HashMap<>();
        for(int i=0;i<result.size();i++){
            List<Map> detailList=JSONArray.parseArray(String.valueOf(result.get(i).get("detail")),Map.class);
            for(int j=0;j<detailList.size();j++){
                uiMap=new HashMap<>();
                uiMap.put("tag",result.get(i).get("tag"));
                uiMap.put("log_time",detailList.get(j).get("log_time"));
                uiMap.put("val",detailList.get(j).get("val"));
                resultList.add(uiMap);
            }
        }
        List<String> tags = new ArrayList();
        List<Map> oclockResults = new ArrayList<>();
        for(int i=0;i<resultList.size();i++){
            Map<String,Object> resultMap = new HashMap<>();
            String tag = String.valueOf(resultList.get(i).get("tag"));
            String log_time = String.valueOf(resultList.get(i).get("log_time"));
            if(("00".equals(log_time.substring(14, 16)))){
                Map<String,Object> val = new HashMap<>();
                val.put("time",Integer.parseInt((String.valueOf(resultList.get(i).get("log_time"))).substring(11,13)));
                val.put("value",Double.valueOf(String.valueOf(resultList.get(i).get("val"))));
                if(!tags.contains(tag)){
                    List<Map> v = new ArrayList<>();
                    tags.add(tag);
                    resultMap.put("name",tag);
                    v.add(val);
                    resultMap.put("data",v);
                    oclockResults.add(resultMap);
                }else{
                    for(int j=0;j<oclockResults.size();j++){
                        if(oclockResults.get(j).get("name").equals(tag)){
                            List<Map> v = (List)oclockResults.get(j).get("data");
                            v.add(val);
                            oclockResults.get(j).remove("data");
                            oclockResults.get(j).put("data",v);
                        }
                    }
                }
            }
        }

        Map<String,Object> vals = new HashMap<>();
        vals.put("branch_name", branch_name);
        vals.put("list", oclockResults);
        return AppResult.success(vals);
    }
}
