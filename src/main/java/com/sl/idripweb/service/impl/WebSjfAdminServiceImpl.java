package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.sl.common.service.CommonService;
import com.sl.common.utils.DateUtil;
import com.sl.common.utils.*;
import com.sl.idripweb.dao.WebSjfAdminDao;
import com.sl.idripweb.service.WebCommonService;
import com.sl.idripweb.service.WebSjfAdminService;
import org.apache.axis.utils.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;


@Service("webSjfAdminServiceImpl")
public class WebSjfAdminServiceImpl implements WebSjfAdminService {

    @Autowired
    private WebSjfAdminDao webSjfAdminDao;
	@Autowired
    DateUtil dateUtil;
	@Autowired
    private CommonService commonService;
    @Autowired
    private RedisPoolUtil redisPoolUtil;


	/**
	 * 园区-建筑接口
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getFactoryBuilding(Map<String, Object> map) {
        List<Map<String, Object>> factoryList = getFactoryList(map);
        return WebResult.success(factoryList);
	}

    /**
     * 园区-电表列表
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getElecMeterList(Map<String, Object> map) {
        PageUtil.setPageInfo(map);
        List<Map<String, Object>> data=  webSjfAdminDao.getElecMeterList(map);
        int total=webSjfAdminDao.getElecMeterListCount(map);
        Map<String, Object> result=new HashMap<>();
        result.put("meterList",data);
        result.put("total",total);
        return WebResult.success(result);
    }

    /**
     * 园区-下载模板
     * @param
     * @param map
     * @return
     */
    @Override
    public void downloadMeter(Map<String, Object> map, HttpServletResponse response) throws Exception{
        //数据
        List<Map<String, Object>> factoryList = getFactoryList(map);
        List<Map> buildingList=new ArrayList<>();
        for(int i=0;i<factoryList.size();i++){
            List<Map<String,Object>> list=(List<Map<String,Object>>)factoryList.get(i).get("building_list");
            for(int j=0;j<list.size();j++){
                buildingList.add(list.get(j));
            }
        }

        //创建excel
        XSSFWorkbook Workbook = ExcelUtil.sjfDownloadMeterExcelFile(factoryList,buildingList,"电表信息导入模板");
        ByteArrayOutputStream out =new  ByteArrayOutputStream();
        //输出流
        Workbook.write(out);
        byte[] bytes = out.toByteArray();
        response.setContentType("application/x-msdownload");
        String time = DateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYYMMDDHHMISS);
        String fileName = "电表信息导入模板_"+time;  //文件名
        fileName = new String(fileName.getBytes(),"ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xlsx");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    /**
     * 园区-导入电表
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult importMeter(Map<String, Object> map, MultipartFile file) {
        List<Map<String,Object>> list=new ArrayList<>();
        //楼区和建筑数据
        Map<String, Object> factory=new HashMap();
        Map<String, Map<String, Object>> factoryBuilding=new HashMap();
        Map<String, Object> building=new HashMap();
        List<Map<String, Object>> factoryList= getFactoryList(map);
        for(int i=0;i<factoryList.size();i++){
            factory.put(factoryList.get(i).get("factory_name").toString(),factoryList.get(i).get("factory_id"));
            List<Map<String,Object>> buildingList=(List<Map<String,Object>>)factoryList.get(i).get("building_list");
            for(int j=0;j<buildingList.size();j++){
                building.put(buildingList.get(j).get("building_name").toString(),buildingList.get(j).get("building_id"));
            }
            factoryBuilding.put(factoryList.get(i).get("factory_name").toString(),building);
        }

        //表号和分户
        List<Map<String, Object>> houseList= webSjfAdminDao.getAllBuildingHouse(map);;

        HashSet<String> numberSet=new HashSet<>();
        HashMap<String,Object> numberMap=new HashMap<>();
        HashMap<String,Map<String,Object>> buildingHouseMap=new HashMap<>();

        HashMap<String,Object> houseNumber=new HashMap<>();
        HashMap<String,Set<String>> buildingHouse=new HashMap<>();
        for(int i=0;i<houseList.size();i++){
            numberSet.add(String.valueOf(houseList.get(i).get("elec_meter_number")));
            numberMap.put(String.valueOf(houseList.get(i).get("elec_meter_number")),i+1);
            houseNumber.put(String.valueOf(houseList.get(i).get("house_number")),houseList.get(i).get("building_id"));
            if(buildingHouse.get(String.valueOf(houseList.get(i).get("building_id")))==null){
                Set<String> houseN=new HashSet<>();
                houseN.add(String.valueOf(houseList.get(i).get("house_number")));
                buildingHouse.put(String.valueOf(houseList.get(i).get("building_id")),houseN);
            }else{
                buildingHouse.get(String.valueOf(houseList.get(i).get("building_id"))).add(String.valueOf(houseList.get(i).get("house_number")));
            }

            if(buildingHouseMap.get(String.valueOf(houseList.get(i).get("building_id")))==null){
                Map<String,Object> houseNumberMap=new HashMap<>();
                houseNumberMap.put(String.valueOf(houseList.get(i).get("house_number")),i+1);
                buildingHouseMap.put(String.valueOf(houseList.get(i).get("building_id")),houseNumberMap);
            }else{
                buildingHouseMap.get(String.valueOf(houseList.get(i).get("building_id"))).put(String.valueOf(houseList.get(i).get("house_number")),i+1);
            }

        }
        int num=houseList.size();




        try {
            InputStream is=file.getInputStream();
            // 工作表
            Workbook workbook = WorkbookFactory.create(is);
            // 获取第一个表的数据
            Sheet sheet = workbook.getSheetAt(0);
            // 行数。
            int rowNumbers = sheet.getLastRowNum()+1;
            //列数 第一行为大标题行  第二列为小标题
            //int cells = sheet.getRow(1).getPhysicalNumberOfCells();
            // 读数据。
            for (int row = 2; row < rowNumbers; row++) {
                Row r = sheet.getRow(row);
                Map<String,Object> rowMap=new HashMap<>();
                int row2=row+1;

                if(r.getCell(0)==null||r.getCell(0).toString().equals("")){
                    return WebResult.error(201,"导入失败,第"+row2+"行`电表编号`为空,请检查!");
                }
                if(r.getCell(1)==null||r.getCell(1).toString().equals("")){
                    return WebResult.error(201,"导入失败,第"+row2+"行`房间号`为空,请检查!");
                }
                if(r.getCell(2)==null||r.getCell(2).toString().equals("")){
                    return WebResult.error(201,"导入失败,第"+row2+"行`所属楼栋`为空,请检查!");
                }
                if(r.getCell(3)==null||r.getCell(3).toString().equals("")){
                    return WebResult.error(201,"导入失败,第"+row2+"行`所属园区`为空,请检查!");
                }
                r.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                r.getCell(1).setCellType(Cell.CELL_TYPE_STRING);

                String elec_meter_number=r.getCell(0).toString(); //电表编号
                String house_number=r.getCell(1).toString(); //房间号
                String building_name=r.getCell(2).toString(); //楼名
                String factory_name=r.getCell(3).toString(); //厂区名
                String building_id=factoryBuilding.get(factory_name).get(building_name).toString(); //楼名


                if(numberSet.add(elec_meter_number)==false){ //已有数据
                    int row3=Integer.parseInt(numberMap.get(elec_meter_number).toString())-num+1;
                    if(row3>1){
                        return WebResult.error(201,"导入失败,第"+row3+"行和"+row2+"行电表编号重复,请检查!");
                    }else{
                        return WebResult.error(201,"导入失败,第"+row2+"行电表编号"+elec_meter_number+"已录入,请检查!");
                    }
                }
                numberMap.put(elec_meter_number,num+row);

                //判断楼宇下边的分户有没有重复
                if(buildingHouse.get(building_id)==null){
                    Set<String> houseN=new HashSet<>();
                    houseN.add(house_number);
                    buildingHouse.put(building_id,houseN);

                    Map<String,Object> houseNumberMap=new HashMap<>();
                    houseNumberMap.put(house_number,num+row);
                    buildingHouseMap.put(building_id,houseNumberMap);
                }else{
                    if(buildingHouse.get(building_id).add(house_number)==false) { //已有数据
                        int row3=Integer.parseInt(buildingHouseMap.get(building_id).get(house_number).toString())-num+1;
                        if(row3>1){
                            return WebResult.error(201,"导入失败,第"+row3+"行和"+row2+"行房间号重复,请检查!");
                        }else{
                            return WebResult.error(201,"导入失败,第"+row2+"行楼宇"+building_name+"下房间号"+house_number+"已录入,请检查!");
                        }
                    }

                    buildingHouseMap.get(building_id).put(house_number,num+row);
                }



                rowMap.put("elec_meter_number",elec_meter_number); //电表编号
                rowMap.put("house_number",house_number); //户号
                rowMap.put("building_id",factoryBuilding.get(factory_name).get(building_name)); //楼名字
                rowMap.put("factory_id",factory.get(factory_name)); //厂区名

                if(r.getCell(4)==null||r.getCell(4).toString().equals("")){
                    rowMap.put("remark",""); //备注
                }else{
                    r.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                    rowMap.put("remark",r.getCell(4).toString()); //备注
                }

                rowMap.put("project_id",map.get("project_id")); //项目
                rowMap.put("elec_meter_id",1788); //暂时用假电表~~~
                list.add(rowMap);
            }
            int result = webSjfAdminDao.addMeter(list);
            if(result>0){
                return WebResult.success(200,"导入成功");
            }else{
                return WebResult.error(202);
            }
        }catch (Exception e){
            return WebResult.error(201,"导入失败,请检查是否按模板格式正确填写!",e);
        }
    }

    /**
     * 增加电表
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult addMeter(Map<String, Object> map){
        if(map.get("factory_id") == null||String.valueOf(map.get("factory_id")).equals("")){
            return WebResult.error(202,"请选择园区!");
        }
        if(map.get("building_id") == null||String.valueOf(map.get("building_id")).equals("")){
            return WebResult.error(202,"请选择楼宇!");
        }
        if(map.get("house_number") == null||String.valueOf(map.get("house_number")).equals("")){
            return WebResult.error(202,"请填写房间号!");
        }
        if(map.get("elec_meter_number") == null||String.valueOf(map.get("elec_meter_number")).equals("")){
            return WebResult.error(202,"请填写电表编号!");
        }



        map.put("elec_meter_id",1788);  //暂时用假表···
        Map<String, Object> param=new HashMap<>();
        param.put("elec_meter_number",map.get("elec_meter_number"));
        param.put("project_id",map.get("project_id"));
        if(webSjfAdminDao.getElecMeterListCount2(param)>0){
            return WebResult.error(201,map.get("elec_meter_number")+" 电表号已经存在!"); //证明这块表的电表编号已经存在
        }
        Map<String, Object> param2=new HashMap<>();
        param2.put("house_number",map.get("house_number"));
        param2.put("building_id",map.get("building_id"));
        param2.put("project_id",map.get("project_id"));
        if(webSjfAdminDao.getElecMeterListCount2(param2)>0){
            return WebResult.error(201,map.get("house_number")+"房间号已经存在!"); //证明这块表的电表编号已经存在
        }

        List<Map<String,Object>> list=new ArrayList<>();
        list.add(map);
        int result = webSjfAdminDao.addMeter(list);
        if(result>0){
            return WebResult.success(200,"保存成功");
        }else{
            return WebResult.error(201);
        }
    }
    /**
     * 增加电表
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult deleteMeter(Map<String, Object> map){
        if (map.get("house_id") == null ||String.valueOf(map.get("house_id")).equals("")){
            return WebResult.error(301);
        }

        Map<String, Object> house= webSjfAdminDao.getHouse(map);
        if(Integer.parseInt(String.valueOf(house.get("is_check_in")))==1){
            return WebResult.error(201,"该电表有分户正在使用!请移除分户再进行此操作!");
        }
        int result = webSjfAdminDao.deleteMeter(map);
        if(result>0){
            return WebResult.success(200,"删除成功");
        }else{
            return WebResult.error(201);
        }
    }

    /**
     * 获取电表信息
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getMeter(Map<String, Object> map) {
        Map<String, Object> meter = webSjfAdminDao.getMeter(map);
       return WebResult.success(meter);
    }

    /**
     * 修改电表
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult updateMeter(Map<String, Object> map) {
        if(map.get("factory_id") == null||String.valueOf(map.get("factory_id")).equals("")){
            return WebResult.error(202,"请选择园区!");
        }
        if(map.get("building_id") == null||String.valueOf(map.get("building_id")).equals("")){
            return WebResult.error(202,"请选择楼宇!");
        }
        if(map.get("house_number") == null||String.valueOf(map.get("house_number")).equals("")){
            return WebResult.error(202,"请填写房间号!");
        }
        if(map.get("elec_meter_number") == null||String.valueOf(map.get("elec_meter_number")).equals("")){
            return WebResult.error(202,"请填写电表编号!");
        }

        if(map.get("house_id") == null||String.valueOf(map.get("house_id")).equals("")){
            return WebResult.error(202,"缺失参数!请检查!");
        }


        Map<String, Object> param=new HashMap<>();
        param.put("elec_meter_number",map.get("elec_meter_number"));
        param.put("project_id",map.get("project_id"));
        param.put("fromNum",0);
        param.put("pageSize",20);
        List<Map<String, Object>> list= webSjfAdminDao.getElecMeterList(param);
        for(int i=0;i<list.size();i++){
            if(!list.get(i).get("house_id").toString().equals(String.valueOf(map.get("house_id")))){
                return WebResult.error(201,map.get("elec_meter_number")+" 电表号已经存在!");
            }
        }
        int result = webSjfAdminDao.updateMeter(map);
        if(result>0){
            return WebResult.success(200,"保存成功");
        }else{
            return WebResult.error(201);
        }
    }

//=================

    /**
     * 用户类型列表
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult houseType(Map<String, Object> map) {
        ArrayList<Map<String, Object>> houseTypes = webSjfAdminDao.houseType(map);
        return WebResult.success(houseTypes);
    }
    /**
     * 分戶列表
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getHouseList(Map<String, Object> map) {
        PageUtil.setPageInfo(map);
        if(map.get("begin_time")==null||map.get("end_time")==null||String.valueOf(map.get("begin_time")).equals("")||String.valueOf(map.get("end_time")).equals("")){
            map.put("begin_time", DateUtil.parseDateToStr(DateUtil.addDate(new Date(), 0, 0, -1, 0, 0, 0, 0), DateUtil.DATE_FORMAT_YYYY_MM_DD)+" 00:00:00");
            map.put("end_time", DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD)+" 23:59:59");
        }
        //列表
        List<Map<String, Object>> houseList=webSjfAdminDao.getHouseList(map);
        int total=webSjfAdminDao.getHouseListCount(map);
        Map<String, Object> result=new HashMap<>();
        if(houseList.size()==0){
            result.put("houseList",houseList);
            result.put("total",total);
            return WebResult.success(result);
        }

        //区间用电
        Map<String,Object> epParam=new HashMap<>();
        epParam.put("begin_time",map.get("begin_time"));
        epParam.put("end_time",map.get("end_time"));
        epParam.put("list",houseList);
        //把eplist转换map 然后存到houseList中
        List<Map<String, Object>> epList=webSjfAdminDao.getEpListByList(epParam);
        Map<Object,Object> epMap=new HashMap<>();
        for(int i=0;i<epList.size();i++){
            epMap.put(epList.get(i).get("meter_id"),epList.get(i).get("ep"));
        }
        double ep=0.00;
        for(int i=0;i<houseList.size();i++){
            ep=epMap.get(houseList.get(i).get("elec_meter_id"))==null?0.00:Double.parseDouble(String.valueOf(epMap.get(houseList.get(i).get("elec_meter_id"))));
            houseList.get(i).put("ep",ep);
        }
        //余额
        houseList=commonService.addBalanceToHouseList(houseList,map);
        //区间缴费金额
        Map<String,Object> prParam=new HashMap<>();
        prParam.put("begin_time",map.get("begin_time"));
        prParam.put("end_time",map.get("end_time"));
        prParam.put("list",houseList);
        List<Map<String, Object>> prList=webSjfAdminDao.getPaymentListByList(epParam);
        Map<Object,Object> prMap=new HashMap<>();
        for(int i=0;i<prList.size();i++){
            prMap.put(prList.get(i).get("house_id"),prList.get(i).get("amount"));
        }
        double amount=0.00;
        for(int i=0;i<houseList.size();i++){
            amount=prMap.get(houseList.get(i).get("house_id"))==null?0.00:Double.parseDouble(String.valueOf(prMap.get(houseList.get(i).get("house_id"))));
            houseList.get(i).put("amount",amount);
        }


        List<Map<String, Object>> resultlist=new ArrayList<>();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        for (int i = 0; i < houseList.size(); i++) {
            Double balance=Double.parseDouble(String.valueOf(houseList.get(i).get("balance")));
            Map<String,Object> resultMap=new HashMap<>();
            resultMap.put("house_id",houseList.get(i).get("house_id"));
            resultMap.put("house_name",houseList.get(i).get("house_name"));
            resultMap.put("type_id",houseList.get(i).get("type_id"));
            resultMap.put("type_name",houseList.get(i).get("type_name"));
            resultMap.put("contact",houseList.get(i).get("contact"));
            resultMap.put("phone",houseList.get(i).get("phone"));
            resultMap.put("elec_meter_number",houseList.get(i).get("elec_meter_number"));
            resultMap.put("balance",new BigDecimal(nf.format((double)Math.round(balance*100)/100)));
            resultMap.put("ep",houseList.get(i).get("ep"));
            resultMap.put("amount",houseList.get(i).get("amount"));
            resultMap.put("factory_name",houseList.get(i).get("factory_name"));
            resultMap.put("building_name",houseList.get(i).get("building_name"));
            resultMap.put("house_number",houseList.get(i).get("house_number"));
            resultlist.add(resultMap);
        }
        result.put("houseList",resultlist);
        result.put("total",total);
        return WebResult.success(result);
    }


    /**
     * 新增分戶
     * @param
     * @param map
     * @return
     */
    @Transactional
    @Override
    public WebResult addHouse(Map<String, Object> map) {
        if(map.get("house_name")==null|| String.valueOf(map.get("house_name")).equals("")){
            return WebResult.error(202,"户名不能为空!");
        }
        if(map.get("elec_meter_number")==null||String.valueOf(map.get("elec_meter_number")).equals("")){
            return WebResult.error(202,"电表编号不能为空!");
        }
        if(map.get("type_id")==null||String.valueOf(map.get("type_id")).equals("") ){
            return WebResult.error(202,"用户类型不能为空!");
        }

        Map<String, Object> house=webSjfAdminDao.getHouseByNumber(map);
        if(house!=null){
            int is_check_in=Integer.parseInt(house.get("is_check_in").toString());
            if(is_check_in==1){
                return WebResult.error(201,"该地址已经有用户入住！");
            }
            map.put("is_check_in", 1); //是否入住改为1
            map.put("supplement_amount", 0); //补加金额改为0
            map.put("cumulative_amount", 0); //累计充值金额改为0
            map.put("purchase_power_time", 0); //购电次数改为0
            map.put("check_in_time",DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD));  //入住时间
            int result=webSjfAdminDao.updateHouseByMeter(map);


            if(result>0){  //修改成功
                commonService.sjfInsertDayData(map.get("project_id").toString());  //插入一遍
                Map<String, Object> pMap=new HashMap<>();
                pMap.put("house_id",house.get("id"));
                pMap.put("project_id",map.get("project_id"));
                Map<String, Object> houseMap=webSjfAdminDao.getHouse(pMap);
                List<Map<String, Object>> houseList=new ArrayList<>();
                houseList.add(houseMap);
                houseList=commonService.addBalanceToHouseList(houseList,pMap); //余额
                Double balance=Double.parseDouble(String.valueOf(houseList.get(0).get("balance")));
                if(balance<0){
                    pMap.put("cumulative_amount", Math.abs(balance)); //累计充值金额改为欠费金额;
                    webSjfAdminDao.updateHouse(pMap);
                }
            }
            if(result>0){
                return WebResult.success(200,"保存成功");
            }else{
                return WebResult.error(201);
            }
        }else{
            return WebResult.error(201,"没有表号信息！请检测电表编号！");
        }
    }


    /**
     * 分戶信息
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getHouseDetail(Map<String, Object> map) {
        if (map.get("house_id") == null||String.valueOf(map.get("house_id")).equals("")) {
            return WebResult.error(301);
        }
        Map<String, Object> house=webSjfAdminDao.getHouse(map);
        Map<String, Object> lastRecord=webSjfAdminDao.getLastPaymentRecord(map);

        List<Map<String, Object>> houseList=new ArrayList<>();
        houseList.add(house);
        houseList=commonService.addBalanceToHouseList(houseList,map);
        house=houseList.get(0);
        //当前日期所在年
        String year = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY);
        Date begin_time=DateUtil.parseStrToDate(year+"-01-01",DateUtil.DATE_FORMAT_YYYY_MM_DD);
        Date check_in_time=DateUtil.parseStrToDate(String.valueOf(house.get("check_in_time")),DateUtil.DATE_FORMAT_YYYY_MM_DD);
        if(begin_time.compareTo(check_in_time)>=0){
            map.put("begin_time",year+"-01-01");
        }else{
            map.put("begin_time",String.valueOf(house.get("check_in_time")));
        }
        map.put("end_time",year+"-12-31");
        Map<String, Object> houseEp=webSjfAdminDao.getHouseEpFees(map);
        Map<String, Object> result=new HashMap<>();
        result.put("factory_id",house.get("factory_id"));
        result.put("factory_name",house.get("factory_name"));
        result.put("building_id",house.get("building_id"));
        result.put("building_name",house.get("building_name"));
        result.put("elec_meter_number",house.get("elec_meter_number"));
        result.put("house_id",house.get("house_id"));
        result.put("house_name",house.get("house_name"));
        result.put("house_number",house.get("house_number"));
        result.put("contact",house.get("contact"));
        result.put("phone",house.get("phone"));
        result.put("type_id",house.get("type_id"));
        result.put("type_name",house.get("type_name"));
        result.put("balance",house.get("balance"));
        result.put("supplement_amount",house.get("supplement_amount"));
        result.put("lastPaymentTime",lastRecord==null?"":lastRecord.get("create_at"));
        result.put("lastPaymentAmount",lastRecord==null?"":lastRecord.get("amount"));
        if(houseEp!=null){
            result.putAll(houseEp);
        }else{
            result.put("peakEp",0);
            result.put("plain_ep",0);
            result.put("valley_ep",0);
            result.put("first_ep",0);
            result.put("second_ep",0);
            result.put("third_ep",0);
            result.put("total_power",0);
        }
        return WebResult.success(result);
    }

    /**
     * 修改分戶
     * @param
     * @param map
     * @return
     */
    @Transactional
    @Override
    public WebResult updateHouse(Map<String, Object> map) {
        if (map.get("house_id") == null || map.get("is_cleared") == null||
            String.valueOf(map.get("house_id")).equals("")||String.valueOf(map.get("is_cleared")).equals("")) {
            return WebResult.error(301);
        }

        if(map.get("house_name")==null|| String.valueOf(map.get("house_name")).equals("")){
            return WebResult.error(202,"户名不能为空!");
        }
        if(map.get("type_id")==null||String.valueOf(map.get("type_id")).equals("") ){
            return WebResult.error(202,"用户类型不能为空!");
        }

        if(map.get("type_id").toString().equals("")){
            map.remove("type_id");
        }
        //清零的情况
        if ("1".equals(String.valueOf(map.get("is_cleared")))) {
            map.put("is_check_in", 1); //是否入住改为1
            map.put("supplement_amount", 0); //补加金额改为0
            map.put("cumulative_amount", 0); //累计充值金额改为0
            map.put("purchase_power_time", 0); //购电次数改为0
            map.put("check_in_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD)); //入住时间 当天
        }
        int result=0;
        if (String.valueOf(map.get("is_cleared")).equals("1")) {
            //获取redis ep实时数据
            Map<String, Object> elec_meter = webSjfAdminDao.getHouse(map); //电表信息
            String _key = elec_meter.get("tg_id") + ":" + elec_meter.get("device_name") + ":ep";
            String value = redisPoolUtil.get(_key);
            if(value==null||value.equals("")){
                map.put("check_in_ep",0);
            }else{
                map.put("check_in_ep", Double.parseDouble(JSONObject.parseObject(value).get("val").toString()));
            }
            result= webSjfAdminDao.updateHouse(map);
            if(result>0){  //修改成功
                commonService.sjfInsertDayData(map.get("project_id").toString());  //插入一遍

                Map<String, Object> pMap=new HashMap<>();
                pMap.put("house_id",map.get("house_id"));
                pMap.put("project_id",map.get("project_id"));
                Map<String, Object> houseMap=webSjfAdminDao.getHouse(pMap);
                List<Map<String, Object>> houseList=new ArrayList<>();
                houseList.add(houseMap);
                houseList=commonService.addBalanceToHouseList(houseList,pMap); //余额
                Double balance=Double.parseDouble(String.valueOf(houseList.get(0).get("balance")));
                if(balance<0){
                    pMap.put("cumulative_amount", Math.abs(balance)); //累计充值金额改为欠费金额;
                    webSjfAdminDao.updateHouse(pMap);
                }
            }

          /* Map<String, Object> epMap = new HashMap<>();
            epMap.put("peak", 0);
            epMap.put("plain", 0);
            epMap.put("valley", 0);
            epMap.put("power", 0);
            epMap.put("meter_id", elec_meter.get("elec_meter_id"));
            epMap.put("date", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
            webSjfAdminDao.updateElecEpToday(epMap);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("house_id",elec_meter.get("house_id"));
            dataMap.put("date", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
            webSjfAdminDao.deleteSjfData(dataMap);*/
        }else{
            result= webSjfAdminDao.updateHouse(map);
        }
        if(result>0){
            return WebResult.success(200,"保存成功");
        }else{
            return WebResult.error(201);
        }
    }
    /**
     * 补加电费
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult supplementFees(Map<String, Object> map) {
        if(map.get("supplement_amount")==null||String.valueOf(map.get("supplement_amount")).equals("")){
            return WebResult.error(201,"请填写补加电费的金额!");
        }
        if(map.get("house_id")==null||String.valueOf(map.get("house_id")).equals("")){
            return WebResult.error(301);
        }
        Map<String, Object> house=webSjfAdminDao.getHouse(map);
        Double supplement_amount = Double.parseDouble(String.valueOf(house.get("supplement_amount")));
        Double new_supplement_amount = supplement_amount + Double.parseDouble(String.valueOf(map.get("supplement_amount")));
        Map<String, Object> params=new HashMap<>();
        params.put("house_id", map.get("house_id"));
        params.put("supplement_amount", new_supplement_amount);
        int result=webSjfAdminDao.updateHouse(params);
        if(result<=0){
            new_supplement_amount = 0.0;
        }
        if(new_supplement_amount==0){
            return WebResult.error(201);
        }else{
            return WebResult.success(200,"保存成功",new_supplement_amount);
        }
    }
    /**
     * 园区-分户下载模板
     * @param
     * @param map
     * @return
     */
    @Override
    public void downloadHouse(Map<String, Object> map, HttpServletResponse response) throws Exception{
        //数据
        List<Map<String, Object>> houseTemplate= webSjfAdminDao.getHouseTemplate(map);
        List<Map<String, Object>> houseType= webSjfAdminDao.houseType(map);

        //创建excel
        XSSFWorkbook Workbook = ExcelUtil.sjfDownloadHouseExcelFile(houseTemplate,houseType,"分户信息导入模板");
        ByteArrayOutputStream out =new  ByteArrayOutputStream();
        //输出流
        Workbook.write(out);
        byte[] bytes = out.toByteArray();
        response.setContentType("application/x-msdownload");
        String time = DateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYYMMDDHHMISS);
        String fileName = "分户信息导入模板_"+time;  //文件名
        fileName = new String(fileName.getBytes(),"ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xlsx");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    /**
     * 园区-导入分户
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult importHouse(Map<String, Object> map, MultipartFile file) {
        ArrayList<ArrayList<Object>> arrayLists = ExcelImportUtil.readExcel(file);
        if(arrayLists == null) return WebResult.error(603);
        List<ArrayList<Object>> newArrayLists = arrayLists.subList(2, arrayLists.size());
        //校验导入的分户数据是否正确
        if(newArrayLists == null){
            return WebResult.error(603);
        }else{
            if(!"收缴费系统用户信息导入模板".equals(arrayLists.get(0).get(0))){
                return WebResult.error(603);
            }
            if(newArrayLists.size() == 0) return WebResult.error(605);
            for(int i=0;i<newArrayLists.size();i++){
                if(StringUtils.isEmpty(String.valueOf(newArrayLists.get(i).get(4)))){
                    return WebResult.error(201,"导入失败,第"+(i+3)+"行`"+String.valueOf(arrayLists.get(1).get(4))+"`为空,请检查!");
                }
                if(StringUtils.isEmpty(String.valueOf(newArrayLists.get(i).get(5)))){
                    return WebResult.error(201,"导入失败,第"+(i+3)+"行`"+String.valueOf(arrayLists.get(1).get(5))+"`为空,请检查!");
                }
            }
        }
        List<Map<String, Object>> houseList = new ArrayList<>();
        //替换解析出来的数据中用户类型为类型id
        List<Map<String, Object>> houseType= webSjfAdminDao.houseType(map);
        Map<String, String> typeMap = new HashMap<>();
        for(Map each : houseType){
            typeMap.put(String.valueOf(each.get("type_name")), String.valueOf(each.get("type_id")));
        }
        for(int i=0;i<newArrayLists.size();i++){
            ArrayList<Object> each = newArrayLists.get(i);
            if(each.get(5) != null) each.set(5, typeMap.get(String.valueOf(each.get(5))));
            Map houseMap = new HashMap();
            houseMap.put("elec_meter_number",each.get(3));
            houseMap.put("house_name",each.get(4));
            houseMap.put("type_id",each.get(5));
            houseMap.put("contact",each.get(6));
            houseMap.put("phone",each.get(7));
            houseList.add(houseMap);
        }
        map.put("houseList", JSON.toJSONString(houseList));
        //批量更新分户信息
        Map<String, Object> resultMap = new HashMap<>();
        String houseListStr = String.valueOf(map.get("houseList"));
//        List<Map<String, Object>> list = new Gson().fromJson(houseListStr, List.class);
        List<Map> list = JSONArray.parseArray(houseListStr, Map.class);
        int successCount = 0;
        int failCount = 0;
        List<String> failMeterList = new ArrayList<>();
        for(Map<String, Object> each : list){
            each.put("is_check_in", 1);
            each.put("check_in_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
            int i = webSjfAdminDao.updateHouseByMeter(each);
            if(i > 0) successCount++;
            else {
                failCount++;
                failMeterList.add(String.valueOf(each.get("elec_meter_number")));
            }
        }
        resultMap.put("successCount", successCount);
        resultMap.put("failCount", failCount);
        resultMap.put("failMeterList", failMeterList);
        return WebResult.success(resultMap);
    }


    /**
     * 园区-获取户号
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getHouseNumber(Map<String, Object> map) {
        //数据
        ArrayList<Map<String, Object>> houseNumber = webSjfAdminDao.getHouseNumber(map);
        return WebResult.success(houseNumber);
    }
    /**
     * 园区-获取电表编号
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getElecMeterNumber(Map<String, Object> map) {
        if(map.get("house_number")==null||String.valueOf(map.get("house_number")).equals("")||
           map.get("building_id")==null||String.valueOf(map.get("building_id")).equals("")){
            return WebResult.error(301);
        }
        //数据
        Map<String, Object> elecMeterNumber = webSjfAdminDao.getElecMeterNumber(map);
        if (elecMeterNumber == null || elecMeterNumber.size() == 0) {
            return WebResult.error(201,"无此地址,请检查门牌号是否正确!");
        }
        //已经有人入住
        if ("1".equals(String.valueOf(elecMeterNumber.get("is_check_in")))) {
            return WebResult.error(201,"此地址已经有分户入住!");
        }
        elecMeterNumber.remove("is_check_in");
        return WebResult.success(200,"   电表编号关联成功!",elecMeterNumber);
    }

    /**
     * 分户删除
     * @param map
     * @return
     */
    @Override
    public WebResult deleteHouse(Map<String, Object> map) {
        if(map.get("house_id")==null||String.valueOf(map.get("house_id")).equals("")){
            return WebResult.error(301);
        }
        int result = webSjfAdminDao.deleteHouse(map);
        if (result > 0) {
            return WebResult.success(200,"删除成功");
        }else{
            return WebResult.error(201);
        }
    }


//========================================================================

    /**
     * 缴费记录
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getPaymentRecord(Map<String, Object> map) {
        PageUtil.setPageInfo(map);
        if(map.get("begin_time")!=null&&map.get("end_time")!=null&&!String.valueOf(map.get("begin_time")).equals("")&&!String.valueOf(map.get("end_time")).equals("")){
            map.put("begin_time", String.valueOf(map.get("begin_time"))+" 00:00:00");
            map.put("end_time", String.valueOf(map.get("end_time"))+" 23:59:59");
        }
        //数据
        List<Map<String, Object>> paymentRecord=  webSjfAdminDao.getPaymentRecord(map);
        int total=  webSjfAdminDao.getPaymentRecordCount(map);
        Map<String, Object> result=new HashMap<>();
        result.put("total",total);
        result.put("recordList",paymentRecord);
        return WebResult.success(result);
    }
    /**
     * 缴费记录
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getAllPaymentRecord(Map<String, Object> map) {
        if(map.get("begin_time")!=null&&map.get("end_time")!=null&&!String.valueOf(map.get("begin_time")).equals("")&&!String.valueOf(map.get("end_time")).equals("")){
            map.put("begin_time", String.valueOf(map.get("begin_time"))+" 00:00:00");
            map.put("end_time", String.valueOf(map.get("end_time"))+" 23:59:59");
        }
        //数据
        List<Map<String, Object>> dataList=webSjfAdminDao.getAllPaymentRecord(map);  //缴费记录

        String column="[{\"prop\":\"house_name\",\"label\":\"分户名\"},{\"prop\":\"elec_meter_number\",\"label\":\"电表编号\"},{\"prop\":\"address\",\"label\":\"用电地址\"},{\"prop\":\"pay_type\",\"label\":\"缴费方式\"},{\"prop\":\"create_at\",\"label\":\"缴费时间\"},{\"prop\":\"amount\",\"label\":\"缴费金额\"},{\"prop\":\"pay_state\",\"label\":\"当前状态\"},{\"prop\":\"odd_number\",\"label\":\"交易单号\"}]";
        List columnList=JSONArray.parseArray(column);
        LinkedHashMap<String, Object> result=new LinkedHashMap<>();
        result.put("column",columnList);
        result.put("data",dataList);
        return WebResult.success(result);
    }



    /**
     * 缴费记录下载
     * @param
     * @param map
     * @return
     */
    @Override
    public void downloadPayment(Map<String, Object> map, HttpServletResponse response) throws Exception{
        //数据
        if(map.get("begin_time")!=null&&map.get("end_time")!=null&&!String.valueOf(map.get("begin_time")).equals("")&&!String.valueOf(map.get("end_time")).equals("")){
            map.put("begin_time", String.valueOf(map.get("begin_time"))+" 00:00:00");
            map.put("end_time", String.valueOf(map.get("end_time"))+" 23:59:59");
        }
        List<Map<String, Object>> paymentData= webSjfAdminDao.downloadPaymentData(map);

        //创建excel
        XSSFWorkbook Workbook = ExcelUtil.sjfDownloadPaymentExcelFile(paymentData,"缴费记录");
        ByteArrayOutputStream out =new  ByteArrayOutputStream();
        //输出流
        Workbook.write(out);
        byte[] bytes = out.toByteArray();
        response.setContentType("application/x-msdownload");
        String time = DateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYYMMDDHHMISS);
        String fileName = "缴费记录_"+time;  //文件名
        fileName = new String(fileName.getBytes(),"ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xlsx");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }




    /**
     * 所有类型列表
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getAllTypeList(Map<String, Object> map) {
        PageUtil.setPageInfo(map);
        //数据
        map.put("date",DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD)); //今天日期
        List<Map<String, Object>> typeList=  webSjfAdminDao.getAllTypeList(map);
        int total=  webSjfAdminDao.getAllTypeListCount(map);
        Map<String, Object> result=new HashMap<>();
        result.put("total",total);
        result.put("typeList",typeList);
        return WebResult.success(result);
    }


    /**
     * 缴费单位
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getChargeUnit(Map<String, Object> map) {
        //数据
        Map<String, Object> chargeUnit = webSjfAdminDao.getChargeUnit(map);
        return WebResult.success(chargeUnit);
    }

    /**
     * 新增分类
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult addHouseType(Map<String, Object> map) {
        if (map.get("type_name") == null||String.valueOf(map.get("type_name")).equals("")){
            return WebResult.error(202,"用户类型不能为空!");
        }
        if (map.get("charge_type") == null||String.valueOf(map.get("charge_type")).equals("")){
            return WebResult.error(202,"收费方式不能为空!");
        }
        int charge_type=Integer.parseInt(String.valueOf(map.get("charge_type")));
        if(charge_type==1){  //平价
           if(map.get("parity_price") == null||String.valueOf(map.get("parity_price")).equals("")){
               return WebResult.error(202,"费率不能为空,请填写费率!");
           }
        }
        if(charge_type==2){  //分时
            if(map.get("peak_price") == null||map.get("plain_price") == null||map.get("valley_price") == null||
            String.valueOf(map.get("peak_price")).equals("")||String.valueOf(map.get("plain_price")).equals("")||String.valueOf(map.get("valley_price")).equals("")){
                return WebResult.error(202,"各时段费率不能为空,请填写费率!");
            }
        }
        if(charge_type==3){  //阶梯
            if(map.get("parity_price") == null||map.get("second_price") == null||map.get("third_price") == null||
             String.valueOf(map.get("parity_price")).equals("")||String.valueOf(map.get("second_price")).equals("")||String.valueOf(map.get("third_price")).equals("")){
                return WebResult.error(202,"费率或阶梯加价不能为空,请填写!");
            }
        }
        if(charge_type==4){  //阶梯+分时
            if(map.get("peak_price") == null||map.get("plain_price") == null||map.get("valley_price") == null||map.get("second_price") == null||map.get("third_price") == null||
              String.valueOf(map.get("peak_price")).equals("")||String.valueOf(map.get("plain_price")).equals("")||String.valueOf(map.get("valley_price")).equals("")
              ||String.valueOf(map.get("second_price")).equals("")||String.valueOf(map.get("third_price")).equals("")
            ){
                return WebResult.error(202,"各时段费率或阶梯加价不能为空,请填写!");
            }
        }


            map.put("set_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
            Map<String, Object> elecSetting = webSjfAdminDao.getHouseTypeByType(map);
            int result;
            if (elecSetting != null) {
                return WebResult.error(201, "用户类型名称重复,请重新输入!");
            } else {
                result = webSjfAdminDao.addHouseType(map);
            }
            if (result==0) {
                return WebResult.error(201);
            }
            return WebResult.success(200,"保存成功");
        }

    /**
     * 分类详情
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getHouseTypeDetail(Map<String, Object> map) {
            if (map.get("type_id") == null||String.valueOf(map.get("type_id")).equals("")) {
                return WebResult.error(301);
            }
            Map<String, Object> elecSetting = webSjfAdminDao.getHouseTypeByType(map);
            Map<String, Object> unit = webSjfAdminDao.getChargeUnit(map);
            elecSetting.put("unit_name", unit.get("unit_name"));
            elecSetting.put("public_account_number", unit.get("public_account_number"));
            return WebResult.success(elecSetting);
        }


    /**
     * 修改缴费分类
     * @param
     * @param map
     * @return
     */
    @Transactional
    @Override
    public WebResult updateHouseType(Map<String, Object> map) {
        if (map.get("type_id") == null||String.valueOf(map.get("type_id")).equals("")) {
            return WebResult.error(301);
        }

        if (String.valueOf(map.get("type_name")).equals("")){
            return WebResult.error(202,"用户类型不能为空!");
        }
        if (map.get("charge_type") == null||String.valueOf(map.get("charge_type")).equals("")){
            return WebResult.error(202,"收费方式不能为空!");
        }
        int charge_type=Integer.parseInt(String.valueOf(map.get("charge_type")));
        if(charge_type==1){  //平价
            if(map.get("parity_price") == null||String.valueOf(map.get("parity_price")).equals("")){
                return WebResult.error(202,"费率不能为空,请填写费率!");
            }
            map.remove("peak_price");
            map.remove("plain_price");
            map.remove("valley_price");
            map.remove("second_price");
            map.remove("third_price");
        }
        if(charge_type==2){  //分时
            if(map.get("peak_price") == null||map.get("plain_price") == null||map.get("valley_price") == null||
                    String.valueOf(map.get("peak_price")).equals("")||String.valueOf(map.get("plain_price")).equals("")||String.valueOf(map.get("valley_price")).equals("")){
                return WebResult.error(202,"各时段费率不能为空,请填写费率!");
            }
            map.remove("second_price");
            map.remove("third_price");
            map.remove("parity_price");
        }
        if(charge_type==3){  //阶梯
            if(map.get("parity_price") == null||map.get("second_price") == null||map.get("third_price") == null||
                    String.valueOf(map.get("parity_price")).equals("")||String.valueOf(map.get("second_price")).equals("")||String.valueOf(map.get("third_price")).equals("")){
                return WebResult.error(202,"费率或阶梯加价不能为空,请填写!");
            }
            map.remove("peak_price");
            map.remove("plain_price");
            map.remove("valley_price");
        }
        if(charge_type==4){  //阶梯+分时
            if(map.get("peak_price") == null||map.get("plain_price") == null||map.get("valley_price") == null||map.get("second_price") == null||map.get("third_price") == null||
                    String.valueOf(map.get("peak_price")).equals("")||String.valueOf(map.get("plain_price")).equals("")||String.valueOf(map.get("valley_price")).equals("")
                    ||String.valueOf(map.get("second_price")).equals("")||String.valueOf(map.get("third_price")).equals("")
            ){
                return WebResult.error(202,"各时段费率或阶梯加价不能为空,请填写!");
            }
            map.remove("parity_price");
        }

        Date today=new Date(); //今天
        Date set_time=DateUtil.parseStrToDate(String.valueOf(map.get("set_time")),DateUtil.DATE_FORMAT_YYYY_MM_DD);  //设置时间
        if(set_time.compareTo(today)<=0){
            return WebResult.error(201,"最近生效时间为下一个自然日,请重新选择!");
        }

        if (map.get("type_name") != null && !String.valueOf(map.get("type_name")).equals("")) {
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put("type_id", map.get("type_id"));
            typeMap.put("project_id", map.get("project_id"));
            Map<String, Object> elecSetting = webSjfAdminDao.getHouseTypeByType(typeMap);
            if(!String.valueOf(elecSetting.get("type_name")).equals(String.valueOf(map.get("type_name")))){
                Map<String, Object> typeMap2 = new HashMap<>();
                typeMap2.put("type_name", map.get("type_name"));
                typeMap2.put("project_id", map.get("project_id"));
                Map<String, Object> elecSetting2 = webSjfAdminDao.getHouseTypeByType(typeMap2);
                if(elecSetting2!=null){
                    return WebResult.error(201, "用户类型名称重复,请重新输入!");
                }
                typeMap.put("type_name", map.get("type_name"));
                webSjfAdminDao.updateHouseType(typeMap);
            }
        }
        Map<String, Object> typeMap=new HashMap<>();
        typeMap.put("type_id",map.get("type_id"));
        webSjfAdminDao.deleteAllHouseTypeFuture(typeMap);
        int result = webSjfAdminDao.addHouseTypeFuture(map);

        if (result==0) {
            return WebResult.error(201);
        }
        return WebResult.success(200,"保存成功");
    }
    /**
     * 峰平谷和阶梯
     * @param
     * @param map
     * @return
     */
    @Override
    public WebResult getFpgAndLadder(Map<String, Object> map) {
        map.put("now_time",DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
        Map<String, Object> result=new HashMap<>();
        result.put("fpg",webSjfAdminDao.getFpg(map));
        result.put("fpgFuture",webSjfAdminDao.getFpgFuture(map));
        result.put("ladder",webSjfAdminDao.getLadder(map));
        result.put("ladderFuture",webSjfAdminDao.getLadderFuture(map));
        return WebResult.success(result);
    }

    /**
     * 峰平谷和阶梯
     * @param
     * @param map
     * @return
     */
    @Transactional
    @Override
    public WebResult updateFpg(Map<String, Object> map) {
        if (map.get("set_time") == null||String.valueOf(map.get("set_time")).equals("")) {
            return WebResult.error(201,"请填写生效时间!");
        }
        if (map.get("peak") == null||map.get("plain") == null||map.get("valley") == null) {
            return WebResult.error(301);
        }
        //判断是否处于一天
        String peak = map.get("peak").toString(); //峰
        String plain = map.get("plain").toString(); //平
        String valley = map.get("valley").toString(); //谷
        String time_str="";
        if (!"".equals(peak)&&!"-".equals(peak)) {
            String type=getFpgSetType(peak);
            if(type.equals("201")){
                return WebResult.error(201,"时间段起始时间应小于结束时间!");
            }
            if(!type.equals("")){
                time_str+= type+",";
            }
        }
        if (!"".equals(plain)&&!"-".equals(plain)) {
            String type=getFpgSetType(plain);
            if(type.equals("201")){
                return WebResult.error(201,"时间段起始时间应小于结束时间!");
            }
            if(!type.equals("")){
                time_str+= type+",";
            }
        }
        if (!"".equals(valley)&&!"-".equals(valley)) {
            String type=getFpgSetType(valley);
            if(type.equals("201")){
                return WebResult.error(201,"时间段起始时间应小于结束时间!");
            }
            time_str+= type;
        }
        List<String> list=Arrays.asList(time_str.split(","));
        if(list.size()!=24){
            return WebResult.error(201,"时间段不能重叠且必须为一整天!");
        }
        Set<String> set=new HashSet<>();
        for(int i=0;i<list.size();i++){
            if(set.add(list.get(i))==false){
                return WebResult.error(201,"时间段不能重叠且必须为一整天!");
            }
        }

        map.put("peak",parseFpgSetType(peak));
        map.put("plain",parseFpgSetType(plain));
        map.put("valley",parseFpgSetType(valley));
        Map<String, Object> fpgMap=new HashMap<>();
        fpgMap.put("project_id",map.get("project_id"));
        webSjfAdminDao.deleteAllFpgFuture(fpgMap);
        int result=webSjfAdminDao.addFpgFutrue(map);
        if (result==0) {
            return WebResult.error(201);
        }
        return WebResult.success(200,"保存成功");
    }

    public String getFpgSetType(String timeType) {
        //14:00-17:00,19:00-22:00
        StringBuffer result = new StringBuffer("");
        String[] splits = timeType.split(",");
        for (int i = 0; i < splits.length; i++) {
            String split =splits[i];
            if(split.equals("-")){
                continue;
            }
            if(split.split("-").length<2){
                continue;
            }
            if(split.split("-")[0].equals("")||split.split("-")[1].equals("")){
                continue;
            }
            int begin = Integer.parseInt(split.split("-")[0].split(":")[0]);
            int end = Integer.parseInt(split.split("-")[1].split(":")[0]);


            if (end == 0) {
                end = 24;
            }
            if(begin>=end){
                return "201";
            }
            for (int j = begin; j < end; j++) {
                result.append(j + ",");
            }
        }
        if (!result.toString().equals("")&&result.toString().substring(result.toString().length() - 1).equals(",")) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }



    public String parseFpgSetType(String timeType) {
        //14:00-17:00,19:00-22:00
        String[] splits = timeType.split(",");
        List<String> splitsList=Arrays.asList(splits);
        List<String> list = new ArrayList<>(splitsList);
        for (int i = 0; i < list.size(); i++) {
            String split = list.get(i);
            if(split.equals("-")){
                list.remove(i);
                i--;
                continue;
            }
            if(split.split("-").length<2){
                list.remove(i);
                i--;
                continue;
            }
            if(split.split("-")[0].equals("")||split.split("-")[1].equals("")){
                list.remove(i);
                i--;
                continue;
            }
        }
        return Joiner.on(",").join(list);
    }



    @Override
    public WebResult deleteLadderFuture(Map<String, Object> map) {
        int result=webSjfAdminDao.deleteAllLadderFuture(map);
        if (result==0) {
            return WebResult.error(201);
        }
        return WebResult.success(200,"删除成功");

    }


    @Override
    public WebResult deleteFpgFuture(Map<String, Object> map) {
        int result=webSjfAdminDao.deleteAllFpgFuture(map);
        if (result==0) {
            return WebResult.error(201);
        }
        return WebResult.success(200,"删除成功");

    }




    /**
     * 峰平谷和阶梯
     * @param
     * @param map
     * @return
     */
    @Transactional
    @Override
    public WebResult updateLadder(Map<String, Object> map) {
        if (map.get("first_ladder") == null||String.valueOf(map.get("first_ladder")).equals("")) {
            return WebResult.error(201,"第一阶梯区间数值不能为空!");
        }
        if (map.get("second_ladder") == null||String.valueOf(map.get("second_ladder")).equals("")) {
            return WebResult.error(201,"第二阶梯区间数值不能为空!");
        }
        if (map.get("set_time") == null||String.valueOf(map.get("set_time")).equals("")) {
            return WebResult.error(201,"请选择生效日期!");
        }
        if(Double.parseDouble(String.valueOf(map.get("second_ladder")))-Double.parseDouble(String.valueOf(map.get("first_ladder")))<=0){
            return WebResult.error(201,"第二阶梯临界值应该大于第一阶梯!");
        }
        Map<String, Object> ladderMap=new HashMap<>();
        ladderMap.put("project_id",map.get("project_id"));
        webSjfAdminDao.deleteAllLadderFuture(ladderMap);
        int result=webSjfAdminDao.addLadderFutrue(map);
        if (result==0) {
            return WebResult.error(201);
        }
        return WebResult.success(200,"保存成功");
    }


    /**
     * 删除电费设置
     * @param
     * @param map
     * @return
     */
    @Transactional
    @Override
    public WebResult deleteHouseType(Map<String, Object> map) {
        if (map.get("type_id") == null||map.get("is_used") == null
                ||String.valueOf(map.get("type_id")).equals("")
                ||String.valueOf(map.get("is_used")).equals("")
        ) {
            return WebResult.error(301);
        }
        map.put("now_time",DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
        int result;
        String is_used=String.valueOf(map.get("is_used")); //判斷是否待生效
        if("1".equals(is_used)){ //使用中
            int houseNumber = webSjfAdminDao.getHouseListCount(map);
            if (houseNumber > 0) {
                result = -1;
            } else {
                webSjfAdminDao.deleteAllHouseTypeFuture(map); //删除待生效
                result = webSjfAdminDao.deleteHouseType(map); //删除正在使用
            }
        }else{
            result = webSjfAdminDao.deleteHouseTypeFuture(map);
        }
        if (result==0) {
            return WebResult.error(201);
        }
        if (result==-1) {
            return WebResult.error(201,"此用户类型有用户正在使用,请先移除用户再进行删除");
        }
        return WebResult.success(200,"删除成功");
    }

    public List<Map<String, Object>> getFactoryList(Map<String, Object> map){
        List<Map<String, Object>>  factoryList=webSjfAdminDao.getFactoryList(map);
        for(int i=0;i<factoryList.size();i++){
            factoryList.get(i).put("building_list",webSjfAdminDao.getBuildingList(factoryList.get(i)));
        }
        return factoryList;
    }


}
