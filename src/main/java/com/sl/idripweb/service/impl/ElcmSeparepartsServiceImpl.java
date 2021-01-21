package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sl.idripweb.dao.ElcmDeviceInfoDao;
import com.sl.idripweb.dao.ElcmSeparepartsDao;
import com.sl.idripweb.service.ElcmSeparepartsService;
import com.sl.common.utils.*;
import com.sl.idripweb.service.WebCommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service("elcmSeparepartsServiceImpl")
public class ElcmSeparepartsServiceImpl implements ElcmSeparepartsService {

	@Autowired
	private ElcmSeparepartsDao elcmSeparepartsDao;
	@Autowired
	private WebCommonService webCommonService;
	@Autowired
	private ElcmDeviceInfoDao elcmDeviceInfoDao;

	//所有列
	private final String columnStr = "[{'prop':'number','label':'备件号'},{'prop':'name','label':'备件名称'},{'prop':'type_name','label':'备件类型'}," +
			"{'prop':'model','label':'规格型号'},{'prop':'length','label':'长'},{'prop':'width','label':'宽'}," +
			"{'prop':'high','label':'高'},{'prop':'manufacturer','label':'生产厂家'},{'prop':'supplier','label':'供应商'}," +
			"{'prop':'minimum_inventory','label':'最低库存'},{'prop':'inventory_unit','label':'库存单位'},{'prop':'assets_name','label':'资产类别'}," +
			"{'prop':'single_price','label':'备件原值（元）'},{'prop':'procurement_cycle','label':'采购周期（天）'},{'prop':'minimum_order_quantity','label':'最小起订量'}," +
			"{'prop':'use_years','label':'使用年限（月）'},{'prop':'position','label':'备件库位'},{'prop':'inventory','label':'库存量'}]";

	//第几列是必须的
	private final String requiredFieldsStr = "1,9,16,17";

	//###############################备件字典表部分###########################################
	//获取所有的备件类型
	@Override
	public WebResult getSeparepartsTypes(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> separepartsTypes = elcmSeparepartsDao.getSeparepartsTypes();
		return WebResult.success(separepartsTypes);
	}

	//获取所有的备件类型
	@Override
	public WebResult getSeparepartsSources(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> separepartsSources = elcmSeparepartsDao.getSeparepartsSources();
		return WebResult.success(separepartsSources);
	}





	//################################备件台账列表部分#############################################
	//备件台账页面根据筛选条件获取备件台账列表
	@Override
	public WebResult getSeparepartsList(Map<String, Object> map) {
		//设置分页信息
		PageUtil.setPageInfo(map);
		//结果集
		HashMap<String, Object> resultMap = new HashMap<>();
		//获取列表
		ArrayList<HashMap<String, Object>> separepartsList = elcmSeparepartsDao.getSeparepartsList(map);
		//获取总数量
		int count = elcmSeparepartsDao.getSeparepartsListCount(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(separepartsList, map);
		resultMap.put("recordList", separepartsList);
		resultMap.put("count", count);
		return WebResult.success(resultMap);
	}

	//备件台账页面导出数据
	@Override
	public void exportSeparepartsList(Map<String, Object> map, HttpServletResponse response) {
		//检查是否传入deviceIds参数
		String sparepartsIds = String.valueOf(map.get("sparepartsIds"));
		if(StringUtils.isEmpty(sparepartsIds)) return;
		String fileName = "备件台账";
		//处理deviceIds为List集合
		ParamConvertUtil.convertIdsString2List(map, "sparepartsIds");
		ArrayList<HashMap<String, Object>> resultData = elcmSeparepartsDao.getExportDataList(map);
		//通用下载方法
		downLoad(response, fileName, null, resultData, "");
	}

	//备件台账页面下载模板
	@Override
	public void downloadTemplate(Map<String, Object> map, HttpServletResponse response) {
		String fileName = "备件台账模板";
		List<Map<String, String>> dropDownConfig = new ArrayList<>();
		//获取所有的备件类型
		ArrayList<HashMap<String, Object>> separepartsTypeList = elcmSeparepartsDao.getSeparepartsTypes();
		//设备类型下拉框设置
		Map<String, String> separepartsTypeMap = new HashMap<>();
		String deviceTypeStr = "";
		for(int i=0;i<separepartsTypeList.size();i++){
			if(i == separepartsTypeList.size() -1)
				deviceTypeStr += String.valueOf(separepartsTypeList.get(i).get("name"));
			else
				deviceTypeStr += String.valueOf(separepartsTypeList.get(i).get("name")) + ",";
		}
		separepartsTypeMap.put("col", "2");
		separepartsTypeMap.put("data", deviceTypeStr);

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
		assetsType.put("col", "11");
		assetsType.put("data", assetsTypeStr);

		dropDownConfig.add(separepartsTypeMap);
		dropDownConfig.add(assetsType);
		downLoad(response, fileName, dropDownConfig, new ArrayList<>(), requiredFieldsStr);
	}

	//备件台账页面模板导入
	@Override
	public WebResult importSepareparts(Map<String, Object> map, MultipartFile file) {
		//最后解析出的所有备件
		List<Map<String, String>> separepartsList = new ArrayList<>();
		ArrayList<ArrayList<Object>> arrayLists = ExcelImportUtil.readExcel(file);
		if(arrayLists == null) return WebResult.error(603);
		//获取文件中的所有备件编号
		List<String> numberList = new ArrayList<>();
		for(ArrayList<Object> each : arrayLists){
			numberList.add(String.valueOf(each.get(0)));
		}
		//设备类型map映射
		Map<String, String> separepartsTypeMap = new HashMap<>();
		//设备资料映射
		Map<String, String> assetsTypeMap = new HashMap<>();
		//获取所有的设备类型
		ArrayList<HashMap<String, Object>> deviceTypeList = elcmSeparepartsDao.getSeparepartsTypes();
		for(Map each : deviceTypeList){
			separepartsTypeMap.put(String.valueOf(each.get("name")), String.valueOf(each.get("id")));
		}
		//获取所有的设备资料
		ArrayList<HashMap<String, Object>> assetsTypeList = elcmDeviceInfoDao.getDeviceMaterial();
		for(Map each : assetsTypeList){
			assetsTypeMap.put(String.valueOf(each.get("assets_name")), String.valueOf(each.get("assets_id")));
		}

		//查询所有备件编号
		List<String> separepartsNumbers = elcmSeparepartsDao.getNumberList(map);

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
					if(j == 0 && separepartsNumbers.contains(eachCol)){
						return WebResult.error(201,"导入失败,第"+(i+1)+"行`设备编号`重复,请检查!");
					}
					//校验文件中的设备编号是否有重复项
					if(j == 0 && numberList.indexOf(eachCol) != numberList.lastIndexOf(eachCol)){
						return WebResult.error(201,"导入失败,第"+((numberList.indexOf(eachCol)+1)+"行和第"+(numberList.lastIndexOf(eachCol)+1)+"行`备件编号`重复,请检查!"));
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
							deviceMap.put("type_id", separepartsTypeMap.get(eachCol));
						else if(j == 11)
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
				separepartsList.add(deviceMap);
			}
		}
		//如果解析出的separepartsList无数据则返回无数据
		if(separepartsList.size() == 0) return WebResult.error(605);
		//存储所有设备
		for(Map each : separepartsList){
			//校验备件编号如果没有就添加
			each.put("project_id", map.get("project_id"));
			checkSeparepartsNumber(each);
		}
		map.put("separepartsList", separepartsList);
		//批量添加备件
		int i = elcmSeparepartsDao.batchAddSepareparts(map);
		if(separepartsList.size() == i) return WebResult.success(200,"导入成功");
		else return WebResult.error(202);
	}

	//选择备件页面根据条件获取备件列表
	@Override
	public WebResult getSeparepartsListForSelect(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> separepartsListForSelect = elcmSeparepartsDao.getSeparepartsListForSelect(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(separepartsListForSelect, map);
		return WebResult.success(separepartsListForSelect);
	}


	//#############################单个备件台账部分################################################
	//单个备件台账信息查询
	@Override
	public WebResult getSeparepartsById(Map<String, Object> map) {
		HashMap<String, Object> separepartsById = elcmSeparepartsDao.getSeparepartsById(map);
		//查询备件关联设备信息
		ArrayList<Integer> devicesRelation = elcmSeparepartsDao.getDevicesRelation(map);
		separepartsById.put("deviceIds", devicesRelation);
		return WebResult.success(separepartsById);
	}

	//添加单个备件
	@Transactional
	@Override
	public WebResult addSingleSepareparts(Map<String, Object> map) {
		checkParams(map);
		//备件编号重复性验证
		List<String> numbers = elcmSeparepartsDao.getNumberList(map);
		if(numbers.contains(String.valueOf(map.get("number")))) return WebResult.error(607);
		//校验备件编号如果没有就添加
		checkSeparepartsNumber(map);
		int flag = elcmSeparepartsDao.addSingleSepareparts(map);
		//添加备件关联设备信息
		addDeviceRelation(map);
		if(flag == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//修改备件信息
	@Transactional
	@Override
	public WebResult updateSepareparts(Map<String, Object> map) {
		checkParams(map);
		//设备编号重复性验证,先查询单个设备再对比
		List<String> numbers = elcmSeparepartsDao.getNumberList(map);
		Map spareParts = elcmSeparepartsDao.getSeparepartsById(map);
		if(!String.valueOf(spareParts.get("number")).equals(String.valueOf(map.get("number"))) && numbers.contains(String.valueOf(map.get("number"))))
			return WebResult.error(607);
		//删除关联设备信息
		elcmSeparepartsDao.deleteDevicesRelation(map);
		int flag = elcmSeparepartsDao.updateSepareparts(map);
		//添加备件关联设备信息
		addDeviceRelation(map);
		if(flag == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//删除备件信息
	@Transactional
	@Override
	public WebResult deleteSepareparts(Map<String, Object> map) {
		//删除关联设备信息
		elcmSeparepartsDao.deleteDevicesRelation(map);
		int i = elcmSeparepartsDao.deleteSepareparts(map);
		if(i == 1) return WebResult.success(200,"删除成功");
		else return WebResult.error(201);
	}








	//#################备件入库################################
	//入库记录查询
	@Override
	public WebResult getSeparepartsInRecord(Map<String, Object> map) {
		//设置分页信息
		PageUtil.setPageInfo(map);
		//结果集
		HashMap<String, Object> resultMap = new HashMap<>();
		//获取列表
		ArrayList<HashMap<String, Object>> separepartsInRecord = elcmSeparepartsDao.getSeparepartsInRecord(map);
		//获取总数量
		int count = elcmSeparepartsDao.getSeparepartsInRecordCount(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(separepartsInRecord, map);
		resultMap.put("recordList", separepartsInRecord);
		resultMap.put("count", count);
		return WebResult.success(resultMap);
	}

	//单条入库记录查询
	@Override
	public WebResult getSeparepartsInById(Map<String, Object> map) {
		HashMap<String, Object> separepartsInByIdData = getSeparepartsInByIdData(map);
		return WebResult.success(separepartsInByIdData);
	}

	//根据入库记录id（多个）查询多条入库记录
	@Override
	public WebResult getSeparepartsInByIds(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		String[] recordIdsArr = String.valueOf(map.get("record_ids")).split(",");
		for(int i= 0;i< recordIdsArr.length;i++){
			map.put("record_id", recordIdsArr[i]);
			HashMap<String, Object> separepartsInById = getSeparepartsInByIdData(map);
			resultList.add(separepartsInById);
		}
		return WebResult.success(resultList);
	}

	//入库单新增
	@Transactional
	@Override
	public WebResult addSeparepartsInRecord(Map<String, Object> map) {
		//生成入库单号
		String elcmOddNumber = webCommonService.getElcmOddNumber("RK", String.valueOf(map.get("project_id")));
		map.put("number", elcmOddNumber);
		//插入入库记录信息
		int flag = elcmSeparepartsDao.addSeparepartsInRecord(map);
		//入库信息插入成功以后的该条记录对应的record_id
		int record_id = Integer.parseInt(String.valueOf(map.get("id")));
		//解析该入库单下的备件采购信息
		String separeparts = String.valueOf(map.get("separeparts"));
		List<Map> separepartsList = JSONArray.parseArray(separeparts, Map.class);
		map.put("separepartsList", separepartsList);
		map.put("record_id", record_id);
		//添加备件采购信息
		elcmSeparepartsDao.addSeparepartsInList(map);
		//更新备件库存信息
		for(Map each : separepartsList){
			map.put("spareparts_id", each.get("spareparts_id"));
			map.put("inventory", each.get("amount"));
			elcmSeparepartsDao.updateSeparepartsInventory(map);
		}
		if(flag == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//入库单修改
	@Transactional
	@Override
	public WebResult updateSeparepartsInRecord(Map<String, Object> map) {
		//修改入库记录信息
		int flag = elcmSeparepartsDao.updateSeparepartsInRecord(map);
		//先查询入库记录下的备件信息
		ArrayList<HashMap<String, Object>> separepartsInList = elcmSeparepartsDao.getSeparepartsInList(map);
		//先删除该入库记录下的备件采购信息
		elcmSeparepartsDao.deleteSeparepartsInList(map);
		//解析该入库单下的备件采购信息
		String separeparts = String.valueOf(map.get("separeparts"));
		List<Map> separepartsList = JSONArray.parseArray(separeparts, Map.class);
		map.put("separepartsList", separepartsList);
		//添加备件采购信息
		elcmSeparepartsDao.addSeparepartsInList(map);
		//更新备件库存信息
		//修改之前先把这些备件数量信息从库存中减掉
		for(HashMap<String, Object> each : separepartsInList){
			map.put("spareparts_id", each.get("spareparts_id"));
			map.put("inventory", -Integer.parseInt(String.valueOf(each.get("amount"))));
			elcmSeparepartsDao.updateSeparepartsInventory(map);
		}
		//然后将备件新数量信息添加进去
		for(Map each : separepartsList){
			map.put("spareparts_id", each.get("spareparts_id"));
			map.put("inventory", Integer.parseInt(String.valueOf(each.get("amount"))));
			elcmSeparepartsDao.updateSeparepartsInventory(map);
		}
		if(flag == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//入库单删除
	@Transactional
	@Override
	public WebResult deleteSeparepartsInRecord(Map<String, Object> map) {
		//更新备件库存信息
		//查询该入库记录下的备件采购列表
		ArrayList<HashMap<String, Object>> separepartsInList = elcmSeparepartsDao.getSeparepartsInList(map);
		for(HashMap<String, Object> each : separepartsInList){
			map.put("spareparts_id", each.get("spareparts_id"));
			map.put("inventory", -Integer.parseInt(String.valueOf(each.get("amount"))));
			elcmSeparepartsDao.updateSeparepartsInventory(map);
		}
		//先删除该入库记录下的备件采购信息
		elcmSeparepartsDao.deleteSeparepartsInList(map);
		//再删除该入库记录信息
		int i = elcmSeparepartsDao.deleteSeparepartsInRecord(map);
		if(i == 1) return WebResult.success(200,"删除成功");
		else return WebResult.error(201);
	}








	//#################备件出库管理################################
	//备件申请列表查询
	@Override
	public WebResult getSeparepartsApply(Map<String, Object> map) {
		//设置分页信息
		PageUtil.setPageInfo(map);
		//结果集
		HashMap<String, Object> resultMap = new HashMap<>();
		//获取列表
		ArrayList<HashMap<String, Object>> separepartsApply = elcmSeparepartsDao.getSeparepartsApply(map);
		//获取总数量
		int count = elcmSeparepartsDao.getSeparepartsApplyCount(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(separepartsApply, map);
		resultMap.put("recordList", separepartsApply);
		resultMap.put("count", count);
		return WebResult.success(resultMap);
	}

	//查询备件申请下的备件清单
	@Override
	public WebResult getSeparepartsApplyOut(Map<String, Object> map) {
		HashMap<String, Object> separepartsApplyOutData = getSeparepartsApplyOutData(map);
		return WebResult.success(separepartsApplyOutData);
	}

	//根据申请记录id（多个）查询多条申请记录
	@Override
	public WebResult getSeparepartsApplyOutByIds(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		String[] applyIdsArr = String.valueOf(map.get("apply_ids")).split(",");
		for(int i= 0;i< applyIdsArr.length;i++){
			map.put("apply_id", applyIdsArr[i]);
			HashMap<String, Object> separepartsApplyOut = getSeparepartsApplyOutData(map);
			resultList.add(separepartsApplyOut);
		}
		return WebResult.success(resultList);
	}

	//出库单新增
	@Transactional
	@Override
	public WebResult addSeparepartsOutRecord(Map<String, Object> map) {
		//生成出库单号
		String elcmOddNumber = webCommonService.getElcmOddNumber("CK", String.valueOf(map.get("project_id")));
		map.put("number", elcmOddNumber);
		//插入出库记录信息
		int flag = elcmSeparepartsDao.addSeparepartsOutRecord(map);
		//出库信息插入成功以后的该条记录对应的record_id
		int record_id = Integer.parseInt(String.valueOf(map.get("id")));
		//解析该入库单下的备件采购信息
		String separeparts = String.valueOf(map.get("separeparts"));
		List<Map> separepartsList = JSONArray.parseArray(separeparts, Map.class);
		map.put("separepartsList", separepartsList);
		map.put("record_id", record_id);
		//更新备件库存信息
		for(Map each : separepartsList){
			map.put("spareparts_id", each.get("spareparts_id"));
			map.put("inventory", -Integer.parseInt(String.valueOf(each.get("amount"))));
			elcmSeparepartsDao.updateSeparepartsInventory(map);
			//判断如果是工单来的备件申请要更新备件申请列表中的已出库数量
			Object applyId = map.get("apply_id");
			if(applyId != null){
				map.put("apply_id",applyId);
				elcmSeparepartsDao.updateSeparepartsApplyOut(map);
			}
		}
		//添加备件出库信息
		elcmSeparepartsDao.addSeparepartsOutList(map);
		if(flag == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//出库明细列表查询
	@Override
	public WebResult getSeparepartsOutRecord(Map<String, Object> map) {
		//设置分页信息
		PageUtil.setPageInfo(map);
		//结果集
		HashMap<String, Object> resultMap = new HashMap<>();
		//获取列表
		ArrayList<HashMap<String, Object>> separepartsOutRecord = elcmSeparepartsDao.getSeparepartsOutRecord(map);
		//获取总数量
		int count = elcmSeparepartsDao.getSeparepartsOutRecordCount(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(separepartsOutRecord, map);
		resultMap.put("recordList", separepartsOutRecord);
		resultMap.put("count", count);
		return WebResult.success(resultMap);
	}

	//出库单修改
	@Transactional
	@Override
	public WebResult updateSeparepartsOutRecord(Map<String, Object> map) {
		//修改出库记录信息
		int flag = elcmSeparepartsDao.updateSeparepartsOutRecord(map);
		//解析该出库单下的备件采购信息
		String separeparts = String.valueOf(map.get("separeparts"));
		List<Map> separepartsList = JSONArray.parseArray(separeparts, Map.class);
		map.put("separepartsList", separepartsList);
		//更新备件库存信息
		//修改之前先把这些备件数量信息从库存中添上
		ArrayList<HashMap<String, Object>> separepartsOutList = elcmSeparepartsDao.getSeparepartsOutList(map);
		for(HashMap<String, Object> each : separepartsOutList){
			map.put("spareparts_id", each.get("spareparts_id"));
			map.put("inventory", Integer.parseInt(String.valueOf(each.get("amount"))));
			elcmSeparepartsDao.updateSeparepartsInventory(map);
		}
		//然后将备件新出库数量信息减去
		for(Map each : separepartsList){
			map.put("spareparts_id", each.get("spareparts_id"));
			map.put("inventory", -Integer.parseInt(String.valueOf(each.get("amount"))));
			elcmSeparepartsDao.updateSeparepartsInventory(map);
		}
		//先删除该出库记录下的备件出库信息
		elcmSeparepartsDao.deleteSeparepartsOutList(map);
		//添加备件出库信息
		elcmSeparepartsDao.addSeparepartsOutList(map);
		if(flag == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//出库单删除
	@Transactional
	@Override
	public WebResult deleteSeparepartsOutRecord(Map<String, Object> map) {
		//更新备件库存信息
		//删除之前先把这些备件数量信息从库存中添上
		ArrayList<HashMap<String, Object>> separepartsOutList = elcmSeparepartsDao.getSeparepartsOutList(map);
		for(HashMap<String, Object> each : separepartsOutList){
			map.put("spareparts_id", each.get("spareparts_id"));
			map.put("inventory", Integer.parseInt(String.valueOf(each.get("amount"))));
			elcmSeparepartsDao.updateSeparepartsInventory(map);
		}
		//先删除该出库记录下的备件出库信息
		elcmSeparepartsDao.deleteSeparepartsOutList(map);
		//再删除该出库记录信息
		int flag = elcmSeparepartsDao.deleteSeparepartsOutRecord(map);
		if(flag == 1) return WebResult.success(200,"删除成功");
		else return WebResult.error(201);
	}

	//备件申请记录新增
	@Transactional
	@Override
	public WebResult addSeparepartsApply(Map<String, Object> map) {
		//插入备件申请信息
		int flag = elcmSeparepartsDao.addSeparepartsApply(map);
		//备件申请信息插入成功以后的该条记录对应的apply_id
		int apply_id = Integer.parseInt(String.valueOf(map.get("id")));
		//解析该入库单下的备件采购信息
		String separeparts = String.valueOf(map.get("separeparts"));
		List<Map> separepartsList = JSONArray.parseArray(separeparts, Map.class);
		map.put("separepartsList", separepartsList);
		map.put("apply_id", apply_id);
		//添加备件申请信息
		elcmSeparepartsDao.addSeparepartsApplyList(map);
		if(flag == 1) return WebResult.success(200,"保存成功");
		else return WebResult.error(201);
	}

	//根据申请备件工单类型和工单号查询所有工单申请记录
	@Override
	public WebResult getSeparepartsApplyList(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> separepartsApplyList = elcmSeparepartsDao.getSeparepartsApplyList(map);
		for(HashMap<String, Object> apply : separepartsApplyList){
			String applyId = String.valueOf(apply.get("apply_id"));
			map.put("apply_id", applyId);
			ArrayList<HashMap<String, Object>> separepartsApplyOutList = elcmSeparepartsDao.getSeparepartsApplyOutList(map);
			//为结果集添加序号
			ResultHandleUtil.addOrderNumber(separepartsApplyOutList, map);
			apply.put("data_list", separepartsApplyOutList);
		}
		return WebResult.success(separepartsApplyList);
	}

	//单条出库记录查询
	@Override
	public WebResult getSeparepartsOutById(Map<String, Object> map) {
		HashMap<String, Object> separepartsOutByIdData = getSeparepartsOutByIdData(map);
		return WebResult.success(separepartsOutByIdData);
	}

	//出库记录批量查询
	@Override
	public WebResult getSeparepartsOutByIds(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		String[] recordIdsArr = String.valueOf(map.get("record_ids")).split(",");
		for(int i= 0;i< recordIdsArr.length;i++){
			map.put("record_id", recordIdsArr[i]);
			HashMap<String, Object> separepartsOutById = getSeparepartsOutByIdData(map);
			resultList.add(separepartsOutById);
		}
		return WebResult.success(resultList);
	}


	//###################################通用方法提取部分#################################################
	//设备添加时校验参数
	private void checkParams(Map<String, Object> map) {
//		Object deviceTypeIdObj = map.get("device_type_id");
//		Object assetsIdObj = map.get("assets_id");
//		if(deviceTypeIdObj == null || StringUtils.isEmpty(String.valueOf(deviceTypeIdObj))) map.put("device_type_id", 1);
//		if(assetsIdObj == null || StringUtils.isEmpty(String.valueOf(assetsIdObj))) map.put("assets_id", 1);
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

	//校验备件编号，如果没有就添加
	private void checkSeparepartsNumber(Map<String, Object> map) {
		Object number = map.get("number");
		if(number == null || "".equals(number)){//此时要为该备件生成备件编号
			String elcmOddNumber = webCommonService.getElcmOddNumber("BJ", String.valueOf(map.get("project_id")));
			map.put("number", elcmOddNumber);
		}
	}

	//添加备件关联设备信息
	private void addDeviceRelation(Map<String, Object> map) {
		if (map.get("deviceIds") != null && !StringUtils.isEmpty(String.valueOf(map.get("deviceIds")))) {
			String deviceIds = String.valueOf(map.get("deviceIds"));
			List<String> deviceIdList = Arrays.asList(deviceIds.split(","));
			map.put("deviceIdList", deviceIdList);
			elcmSeparepartsDao.addDevicesRelation(map);
		}
	}

	private HashMap<String, Object> getSeparepartsInByIdData(Map<String, Object> map) {
		//查询入库记录
		HashMap<String, Object> separepartsInById = elcmSeparepartsDao.getSeparepartsInById(map);
		//查询该入库记录下的备件采购列表
		ArrayList<HashMap<String, Object>> separepartsInList = elcmSeparepartsDao.getSeparepartsInList(map);
		ResultHandleUtil.addOrderNumber(separepartsInList, map);
		separepartsInById.put("separepartsInList", separepartsInList);
		return separepartsInById;
	}

	public HashMap<String, Object> getSeparepartsApplyOutData(Map<String, Object> map) {
		//根据备件申请id查询单个备件申请
		HashMap<String, Object> separepartsApplyById = elcmSeparepartsDao.getSeparepartsApplyById(map);
		//查询备件申请下的备件清单
		ArrayList<HashMap<String, Object>> separepartsApply = elcmSeparepartsDao.getSeparepartsApplyOut(map);
		//为结果集添加序号
		ResultHandleUtil.addOrderNumber(separepartsApply, map);
		separepartsApplyById.put("separepartsOutList", separepartsApply);
		return separepartsApplyById;
	}

	public HashMap<String, Object> getSeparepartsOutByIdData(Map<String, Object> map) {
		//查询出库记录
		HashMap<String, Object> separepartsOutById = elcmSeparepartsDao.getSeparepartsOutById(map);
		//查询该入库记录下的备件采购列表
		ArrayList<HashMap<String, Object>> separepartsOutList = elcmSeparepartsDao.getSeparepartsOutList(map);
		separepartsOutById.put("separepartsOutList", separepartsOutList);
		return separepartsOutById;
	}


}
