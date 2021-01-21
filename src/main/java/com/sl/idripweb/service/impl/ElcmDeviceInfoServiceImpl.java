package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sl.idripapp.entity.ElcmDeviceTypeTree;
import com.sl.idripweb.dao.ElcmDeviceInfoDao;
import com.sl.idripweb.service.ElcmDeviceInfoService;
import com.sl.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("deviceInfoServiceImpl")
public class ElcmDeviceInfoServiceImpl implements ElcmDeviceInfoService {

	@Autowired
	private ElcmDeviceInfoDao elcmDeviceInfoDao;

	final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//所有列
	private final String columnStr = "[{'prop':'device_number','label':'设备编号'},{'prop':'device_name','label':'设备名称'},{'prop':'device_type_name','label':'设备类型'}," +
			"{'prop':'affiliated_unit','label':'所属单位'},{'prop':'use_unit','label':'使用单位'},{'prop':'install_position','label':'设备位置'}," +
			"{'prop':'assets_name','label':'资产类别'},{'prop':'brand','label':'品牌'},{'prop':'supplier','label':'供应商'}," +
			"{'prop':'device_model','label':'规格型号'},{'prop':'use_years','label':'使用年限（年）'},{'prop':'price','label':'设备原值（元）'}," +
			"{'prop':'number','label':'数量'},{'prop':'project_name','label':'所属项目'},{'prop':'install_time','label':'安装时间'}," +
			"{'prop':'construction_unit','label':'施工单位'},{'prop':'contact_construction_unit','label':'施工单位联系方式'},{'prop':'unit','label':'维保单位'}," +
			"{'prop':'contact_unit','label':'维保单位联系方式'},{'prop':'manufactor','label':'生产厂家'},{'prop':'contact_manufactor','label':'生产厂家联系方式'}," +
			"{'prop':'remarks','label':'备注'}]";

	//第几列是必须的
	private final String requiredFieldsStr = "0,1,2,3,4,5,6,9";


	//###############################设备字典表部分###########################################

	//获取所有的设备类型树
	@Override
	public WebResult getDeviceTypeTree(Map<String, Object> map) {
		//所有树的数据
		ArrayList<ElcmDeviceTypeTree> typeList=elcmDeviceInfoDao.getDeviceTypeTree(map);
		//设备数量
		ArrayList<Map<String,Object>> deviceCountList=elcmDeviceInfoDao.getDeviceCountByType(map);
		Map<Integer, Integer> deviceCount=new HashMap<>();
		for (Map<String,Object> item:deviceCountList){
			deviceCount.put(Integer.parseInt(item.get("device_type_id").toString()),Integer.parseInt(item.get("deviceCount").toString()));
		}

		ElcmDeviceTypeTree elcmDeviceTypeTree=new ElcmDeviceTypeTree();
		//递归获取树结构
		List<ElcmDeviceTypeTree> result=elcmDeviceTypeTree.getElcmDeviceTypeTreeByRecursion(0,typeList,deviceCount);
		return WebResult.success(result);
	}



	//获取所有的设备类型
	@Override
	public WebResult getDeviceTypes(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> deviceTypes = elcmDeviceInfoDao.getDeviceTypes();
		return WebResult.success(deviceTypes);
	}

	//获取所有的设备状态
	@Override
	public WebResult getDeviceStatus(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> deviceStatus = elcmDeviceInfoDao.getDeviceStatus();
		return WebResult.success(deviceStatus);
	}

	//获取所有的资产类别
	@Override
	public WebResult getDeviceMaterial(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> deviceMaterial = elcmDeviceInfoDao.getDeviceMaterial();
		return WebResult.success(deviceMaterial);
	}


	//################################设备列表部分#############################################
	//设备台账页面根据筛选条件获取设备列表
	@Override
	public WebResult getDeviceList(Map<String, Object> map) {
		//设置分页信息
		PageUtil.setPageInfo(map);
		//结果集
		HashMap<String, Object> resultMap = new HashMap<>();
		//获取列表
		ArrayList<HashMap<String, Object>> deviceList = elcmDeviceInfoDao.getDeviceList(map);
		//获取总数量
		int count = elcmDeviceInfoDao.getDeviceListCount(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(deviceList, map);
		resultMap.put("recordList", deviceList);
		resultMap.put("count", count);
		return WebResult.success(resultMap);
	}

	//设备台账页面导出数据
	@Override
	public void exportDeviceList(Map<String, Object> map, HttpServletResponse response) {
		//检查是否传入deviceIds参数
		String deviceIds = String.valueOf(map.get("deviceIds"));
		if(StringUtils.isEmpty(deviceIds)) return;
		String fileName = "设备台账";
		//处理deviceIds为List集合
		ParamConvertUtil.convertIdsString2List(map, "deviceIds");
		ArrayList<HashMap<String, Object>> resultData = elcmDeviceInfoDao.getExportDataList(map);
		//通用下载方法
		downLoad(response, fileName, null, resultData, "");
	}

	//设备台账页面下载模板
	@Override
	public void downloadTemplate(Map<String, Object> map, HttpServletResponse response) {
		String fileName = "设备台账模板";
		List<Map<String, String>> dropDownConfig = new ArrayList<>();
		//获取所有的设备类型
		ArrayList<HashMap<String, Object>> deviceTypeList = elcmDeviceInfoDao.getDeviceTypes();
		//设备类型下拉框设置
		Map<String, String> deviceTypeMap = new HashMap<>();
		String deviceTypeStr = "";
		for(int i=0;i<deviceTypeList.size();i++){
			if(i == deviceTypeList.size() -1)
				deviceTypeStr += String.valueOf(deviceTypeList.get(i).get("device_type_name"));
			else
				deviceTypeStr += String.valueOf(deviceTypeList.get(i).get("device_type_name")) + ",";
		}
		deviceTypeMap.put("col", "2");
		deviceTypeMap.put("data", deviceTypeStr);

		//获取所有的设备资料
		ArrayList<HashMap<String, Object>> assetsTypeList = elcmDeviceInfoDao.getDeviceMaterial();
		//设备资产下拉框设置
		Map<String, String> assetsType = new HashMap<>();
		String assetsTypeStr = "";
		for(int i=0;i<assetsTypeList.size();i++){
			if(i == assetsTypeList.size() -1)
				assetsTypeStr += String.valueOf(assetsTypeList.get(i).get("assets_name"));
			else
				assetsTypeStr += String.valueOf(assetsTypeList.get(i).get("assets_name")) + ",";
		}
		assetsType.put("col", "6");
		assetsType.put("data", assetsTypeStr);

		dropDownConfig.add(deviceTypeMap);
		dropDownConfig.add(assetsType);
		downLoad(response, fileName, dropDownConfig, new ArrayList<>(), requiredFieldsStr);
	}

	//设备台账页面模板导入
	@Override
	public WebResult importDevices(Map<String, Object> map, MultipartFile file) {
		//最后解析出的所有设备
		List<Map<String, String>> deviceList = new ArrayList<>();
		ArrayList<ArrayList<Object>> arrayLists = ExcelImportUtil.readExcel(file);
		if(arrayLists == null) return WebResult.error(603);
		//获取文件中的所有设备编号
		List<String> numberList = new ArrayList<>();
		for(ArrayList<Object> each : arrayLists){
			numberList.add(String.valueOf(each.get(0)));
		}

		//设备类型map映射
		Map<String, String> deviceTypeMap = new HashMap<>();
		//设备资料映射
		Map<String, String> assetsTypeMap = new HashMap<>();
		//获取所有的设备类型
		ArrayList<HashMap<String, Object>> deviceTypeList = elcmDeviceInfoDao.getDeviceTypes();
		for(Map each : deviceTypeList){
			deviceTypeMap.put(String.valueOf(each.get("device_type_name")), String.valueOf(each.get("device_type_id")));
		}
		//获取所有的设备资料
		ArrayList<HashMap<String, Object>> assetsTypeList = elcmDeviceInfoDao.getDeviceMaterial();
		for(Map each : assetsTypeList){
			assetsTypeMap.put(String.valueOf(each.get("assets_name")), String.valueOf(each.get("assets_id")));
		}

		//查询所有设备编号
		List<String> deviceNumbers = elcmDeviceInfoDao.getDeviceNumberList(map);

		//检查模板表头是否被修改
		JSONArray column = JSONArray.parseArray(columnStr);
		List<String> allTitle = new ArrayList<>();
		List<String> allProps = new ArrayList<>();
		for(int i=0;i<column.size();i++) {
			allTitle.add(String.valueOf(column.getJSONObject(i).get("label")));
			allProps.add(String.valueOf(column.getJSONObject(i).get("prop")));
		}
		//获取必填字段列
		List<String> requiredFieldList = Arrays.asList(requiredFieldsStr.split(","));
		//存储比填列字段名称
		Map<String, String> fieldMap = new HashMap<>();
		for(int i=0;i<arrayLists.size();i++){
			ArrayList<Object> eachRow = arrayLists.get(i);
			Map<String, String> deviceMap = new HashMap<>();
			for(int j=0;j<eachRow.size();j++){
				String eachCol = String.valueOf(eachRow.get(j));
				if(i == 0) {//表头行
					//校验表头是否正确
					if(!eachCol.equals(allTitle.get(j))){
						return WebResult.error(201,"导入失败,第"+(j+1)+"列表头`"+allTitle.get(j)+"`被修改为`"+eachCol+"`,请检查!");
					}
					fieldMap.put(String.valueOf(j), eachCol);
				}else{//非表头行
					//校验设备编号是否重复
					if(j == 0 && deviceNumbers.contains(eachCol)){
						return WebResult.error(201,"导入失败,第"+(i+1)+"行`设备编号`重复,请检查!");
					}
					//校验文件中的设备编号是否有重复项
					if(j == 0 && numberList.indexOf(eachCol) != numberList.lastIndexOf(eachCol)){
						return WebResult.error(201,"导入失败,第"+((numberList.indexOf(eachCol)+1)+"行和第"+(numberList.lastIndexOf(eachCol)+1)+"行`设备编号`重复,请检查!"));
					}
					//校验必填项
					if(requiredFieldList.contains(String.valueOf(j))){
						if(eachCol == null || StringUtils.isEmpty(eachCol)){
							return WebResult.error(201,"导入失败,第"+(i+1)+"行`"+fieldMap.get(String.valueOf(j))+"`为空,请检查!");
						}
					}
					//添加不为空的字段到map中
					if(eachCol != null && !StringUtils.isEmpty(eachCol)){
						//该行记录校验通过，存储到deviceList结果集中，判断如果是设备类型或者资产类型要进行映射
						if(j == 2)
							deviceMap.put("device_type_id", deviceTypeMap.get(eachCol));
						else if(j == 6)
							deviceMap.put("assets_id", assetsTypeMap.get(eachCol));
						else
							deviceMap.put(allProps.get(j), eachCol);
					}
				}
			}
			//校验必填项
			if(i !=0){
				for(String requiredIndex : requiredFieldList){
					if(Integer.parseInt(requiredIndex) >= eachRow.size()){
						return WebResult.error(201,"导入失败,第"+(i+1)+"行`"+fieldMap.get(requiredIndex)+"`为空,请检查!");
					}
				}
				//判断安装时间为空就设置默认今天
				if(deviceMap.get("install_time") == null || StringUtils.isEmpty(deviceMap.get("install_time"))){
					deviceMap.put("install_time", DateUtil.parseDateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
				}
				deviceList.add(deviceMap);
			}
		}
		//如果解析出的deviceList无数据则返回无数据
		if(deviceList.size() == 0) return WebResult.error(605);
		//存储所有设备
		map.put("deviceList", deviceList);
		//将所有设备集合字符串转为集合
		int i = elcmDeviceInfoDao.batchAddDevice(map);
		if(deviceList.size()== i) return WebResult.success(200,"导入成功");
		else return WebResult.error(202);
	}

	//选择设备页面根据条件获取设备列表
	@Override
	public WebResult getDeviceListForSelect(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> deviceList = elcmDeviceInfoDao.getDeviceListForSelect(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(deviceList, map);
		return WebResult.success(deviceList);
	}

	//获取设备台账页批量打印接口数据
	@Override
	public WebResult getPrintDataList(Map<String, Object> map) {
		//检查是否传入deviceIds参数
		String deviceIds = String.valueOf(map.get("deviceIds"));
		if(StringUtils.isEmpty(deviceIds)) return WebResult.error(301);
		//处理deviceIds为List集合
		ParamConvertUtil.convertIdsString2List(map, "deviceIds");
		ArrayList<HashMap<String, Object>> printDataList = elcmDeviceInfoDao.getPrintDataList(map);
		return WebResult.success(printDataList);
	}


	//#############################设备基础信息部分################################################
	//设备台账页面基础信息查询
	@Override
	public WebResult getDeviceById(Map<String, Object> map) {
		HashMap<String, Object> deviceById = elcmDeviceInfoDao.getDeviceById(map);
		return WebResult.success(deviceById);
	}

	//添加单个设备
	@Override
	public WebResult addSingleDevice(Map<String, Object> map) {
		//参数校验
		checkParams(map);
		//设备编号重复性验证
		List<String> deviceNumbers = elcmDeviceInfoDao.getDeviceNumberList(map);
		if(deviceNumbers.contains(String.valueOf(map.get("device_number")))) return WebResult.error(606);
		int i = elcmDeviceInfoDao.addSingleDevice(map);
		if(i == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//修改设备信息
	@Override
	public WebResult updateDevice(Map<String, Object> map) {
		//参数校验
		checkParams(map);
		//设备编号重复性验证,先查询单个设备再对比
		List<String> deviceNumbers = elcmDeviceInfoDao.getDeviceNumberList(map);
		Map device = elcmDeviceInfoDao.getDeviceById(map);
		if(!String.valueOf(device.get("device_number")).equals(String.valueOf(map.get("device_number"))) && deviceNumbers.contains(String.valueOf(map.get("device_number"))))
			return WebResult.error(606);
		int i = elcmDeviceInfoDao.updateDevice(map);
		if(i == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}



	//#################################设备组成元件列表部分########################################################
	//组成元件列表查询
	@Override
	public WebResult getComponentList(Map<String, Object> map) {
		//设置分页信息
		PageUtil.setPageInfo(map);
		//结果集
		HashMap<String, Object> resultMap = new HashMap<>();
		//获取列表
		ArrayList<HashMap<String, Object>> componentList = elcmDeviceInfoDao.getComponentList(map);
		//获取总数量
		int count = elcmDeviceInfoDao.getComponentListCount(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(componentList, map);
		resultMap.put("recordList", componentList);
		resultMap.put("count", count);
		return WebResult.success(resultMap);
	}

	//根据元器件id查询单个设备元器件
	@Override
	public WebResult getComponentById(Map<String, Object> map) {
		HashMap<String, Object> componentById = elcmDeviceInfoDao.getComponentById(map);
		return WebResult.success(componentById);
	}

	//新增设备元器件
	@Override
	public WebResult addComponent(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.addComponent(map);
		if(i == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//修改设备元器件
	@Override
	public WebResult updateComponent(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.updateComponent(map);
		if(i == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//删除设备元器件
	@Override
	public WebResult deleteComponent(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.deleteComponent(map);
		if(i == 1) return WebResult.success(200,"删除成功");
		else return WebResult.error(201);
	}




	//####################################设备技术参数部分##################################################
	//技术参数列表查询
	@Override
	public WebResult getTechnicalParamList(Map<String, Object> map) {
		//设置分页信息
		PageUtil.setPageInfo(map);
		//结果集
		HashMap<String, Object> resultMap = new HashMap<>();
		//获取列表
		ArrayList<HashMap<String, Object>> technicalParamList = elcmDeviceInfoDao.getTechnicalParamList(map);
		//获取总数量
		int count = elcmDeviceInfoDao.getTechnicalParamListCount(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(technicalParamList, map);
		resultMap.put("recordList", technicalParamList);
		resultMap.put("count", count);
		return WebResult.success(resultMap);
	}

	//根据技术参数id查询单个技术参数
	@Override
	public WebResult getTechnicalParamById(Map<String, Object> map) {
		HashMap<String, Object> technicalParamById = elcmDeviceInfoDao.getTechnicalParamById(map);
		return WebResult.success(technicalParamById);
	}

	//新增设备技术参数
	@Override
	public WebResult addTechnicalParam(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.addTechnicalParam(map);
		if(i == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//修改设备技术参数
	@Override
	public WebResult updateTechnicalParam(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.updateTechnicalParam(map);
		if(i == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//删除设备技术参数
	@Override
	public WebResult deleteTechnicalParam(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.deleteTechnicalParam(map);
		if(i == 1) return WebResult.success(200,"删除成功");
		else return WebResult.error(201);
	}



	//########################################设备维保记录部分######################################
	//维保记录列表查询,包含count
	@Override
	public WebResult getMaintenanceRecordList(Map<String, Object> map) {
		//设置分页信息
		PageUtil.setPageInfo(map);
		HashMap<String, Object> resultMap = new HashMap<>();
		//查询列表数据
		ArrayList<HashMap<String, Object>> maintenanceRecordList = elcmDeviceInfoDao.getMaintenanceRecordList(map);
		//查询统计数据
		ArrayList<HashMap<String, Object>> maintenanceRecordCount = elcmDeviceInfoDao.getMaintenanceRecordCount(map);
		//获取总数量
		int count = elcmDeviceInfoDao.getMaintenanceRecordListCount(map);
		resultMap.put("counting", maintenanceRecordCount);
		resultMap.put("recordList", maintenanceRecordList);
		resultMap.put("count", count);
		return WebResult.success(resultMap);
	}





	//##############################设备保养清单部分#######################################
	//保养清单列表查询
	@Override
	public WebResult getMaintainList(Map<String, Object> map) {
		//设置分页信息
		PageUtil.setPageInfo(map);
		//结果集
		HashMap<String, Object> resultMap = new HashMap<>();
		//获取列表
		ArrayList<HashMap<String, Object>> maintainList = elcmDeviceInfoDao.getMaintainList(map);
		//获取总数量
		int count = elcmDeviceInfoDao.getMaintainListCount(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(maintainList, map);
		resultMap.put("recordList", maintainList);
		resultMap.put("count", count);
		return WebResult.success(resultMap);
	}

	//根据保养清单id查询单个保养清单
	@Override
	public WebResult getMaintainById(Map<String, Object> map) {
		HashMap<String, Object> maintainById = elcmDeviceInfoDao.getMaintainById(map);
		return WebResult.success(maintainById);
	}

	//新增设备保养清单
	@Override
	public WebResult addMaintain(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.addMaintain(map);
		if(i == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//修改设备保养清单
	@Override
	public WebResult updateMaintain(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.updateMaintain(map);
		if(i == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//删除设备保养清单
	@Override
	public WebResult deleteMaintain(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.deleteMaintain(map);
		if(i == 1) return WebResult.success(200,"删除成功");
		else return WebResult.error(201);
	}




	//#################################设备资料部分###############################################
	//设备资料列表查询
	@Override
	public WebResult getMaterialList(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> materialList = elcmDeviceInfoDao.getMaterialList(map);
		return WebResult.success(materialList);
	}

	//新增设备资料
	@Override
	public WebResult addMaterial(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.addMaterial(map);
		if(i == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//删除设备资料
	@Override
	public WebResult deleteMaterial(Map<String, Object> map) {
		int i = elcmDeviceInfoDao.deleteMaterial(map);
		if(i == 1) return WebResult.success(200,"删除成功");
		else return WebResult.error(201);
	}




	//###################################通用方法提取部分#################################################
	//设备添加时校验参数
	private void checkParams(Map<String, Object> map) {
		Object deviceTypeIdObj = map.get("device_type_id");
		Object assetsIdObj = map.get("assets_id");
		Object useYearsObj = map.get("use_years");
		Object priceObj = map.get("price");
		Object numberObj = map.get("number");
		Object installTimeObj = map.get("install_time");

		if(deviceTypeIdObj == null || StringUtils.isEmpty(String.valueOf(deviceTypeIdObj))) map.put("device_type_id", 1);
		if(assetsIdObj == null || StringUtils.isEmpty(String.valueOf(assetsIdObj))) map.put("assets_id", 1);
		if(useYearsObj == null || StringUtils.isEmpty(String.valueOf(useYearsObj))) map.put("use_years", 0);
		if(priceObj == null || StringUtils.isEmpty(String.valueOf(priceObj))) map.put("price", 0.00);
		if(numberObj == null || StringUtils.isEmpty(String.valueOf(numberObj))) map.put("number", 1);
		if(installTimeObj == null || StringUtils.isEmpty(String.valueOf(installTimeObj))) map.put("install_time", sf.format(new Date()));
	}

	//封装下载逻辑
	public void downLoad(HttpServletResponse response, String fileName, List<Map<String, String>> dropDownConfig, ArrayList<HashMap<String, Object>> resultData, String requiredFields){
		JSONArray column = JSONArray.parseArray(columnStr);
		List<String> title = new ArrayList<>();//设置表格表头字段
		List<String> properties = new ArrayList<>();// 查询对应的字段
		for(int i=0;i<column.size();i++) {
			title.add(String.valueOf(column.getJSONObject(i).get("label")));
			properties.add(String.valueOf(column.getJSONObject(i).get("prop")));
		}
		ExcelExportUtil excelExportUtil = new ExcelExportUtil();
		excelExportUtil.setData(resultData);
		excelExportUtil.setHeardKey(properties);
		excelExportUtil.setFontSize(11);
		excelExportUtil.setSheetName(fileName);
		excelExportUtil.setFileName(fileName);
		excelExportUtil.setHeardList(title);
		excelExportUtil.setRequiredFields(requiredFields);
		excelExportUtil.setDropDownConfig(dropDownConfig);
		try {
			excelExportUtil.exportExport(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
