package com.sl.idripapp.service.impl;

import com.sl.idripapp.dao.ElcmDeviceTaskDao;
import com.sl.idripapp.dao.ElcmMalfunctionDao;
import com.sl.idripapp.service.ElcmMalfunctionService;
import com.sl.common.utils.*;
import com.sl.idripweb.service.WebCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("elcmMalfunctionServiceImpl")
public class ElcmMalfunctionServiceImpl implements ElcmMalfunctionService {

	@Autowired
	private ElcmMalfunctionDao elcmMalfunctionDao;
	@Autowired
	private ElcmDeviceTaskDao deviceTaskDao;
	@Autowired
	private WebCommonService webCommonService;
	@Autowired
	private DateUtil dateUtil;

	/**
	 *获取故障
	 * @param
	 * @return
	 */
	@Override
	public AppResult getMalfunctionList(Map<String, Object> map) {
		if(map.get("pageNum")==null||map.get("pageSize")==null){
			map.put("pageNum", 1);
			map.put("pageSize", 20);
		}
		int fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
		map.put("fromNum",fromNum);
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		if(map.get("is_complete")==null){
			map.put("is_complete",-1);
		}
		Map<String, Object> result = new HashMap<>();
		List<Map<String,Object>> list=elcmMalfunctionDao.getMalfunctionList(map);

		for(int i=0;i<list.size();i++){
			if(list.get(i).get("device_url")==null||String.valueOf(list.get(i).get("device_url")).equals("")){
				list.get(i).put("device_url",new ArrayList<>());
			}else{
				List<String> device_url = Arrays.asList(String.valueOf(list.get(i).get("device_url")).split(","));
				list.get(i).put("device_url",device_url);
			}
		}

		int pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
		int total = elcmMalfunctionDao.getMalfunctionListCount(map);
		if ((fromNum + pageSize) >= total) {
			result.put("is_lastpage", true);
		} else {
			result.put("is_lastpage", false);
		}
		result.put("malfunctionList",list);
		result.put("total",total);
		return AppResult.success(result);
	}

	/**
	 *获取已经存在的故障
	 * @param
	 * @return
	 */
	@Override
	public AppResult getExistingMalfunction(Map<String, Object> map) {
		if(map.get("device_id")==null){
			return AppResult.error("1003");
		}
		ArrayList<Map<String, Object>> existingMalfunction = elcmMalfunctionDao.getExistingMalfunction(map);
		return AppResult.success(existingMalfunction);
	}


	/**
	 *上报故障
	 * @param
	 * @return
	 */
	@Transactional
	@Override
	public AppResult reportMalfunction(Map<String, Object> map) {
		if(map.get("device_id")==null||map.get("urgency")==null||map.get("is_repair")==null){
			return AppResult.error("1003");
		}
		int is_repair=Integer.parseInt(String.valueOf(map.get("is_repair")));
		if(is_repair==0){  //不转维修
			if(map.get("repair_describe")==null){
				return AppResult.error("1003");
			}
		}
		map.put("report_user",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
		map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
		if(is_repair==1){
			map.put("status",1); //转维修 维修中
		}else{
			map.put("status",2); //不转维修  已完成
		}
		String number=webCommonService.getElcmOddNumber("BX",String.valueOf(map.get("project_id")));
		map.put("malfunction_number",number); //单号
		int result=elcmMalfunctionDao.reportMalfunction(map);
		if(result>0){  //上报成功
			if(is_repair==1){  //如果是转维修  添加维修信息
				String repair_number=webCommonService.getElcmOddNumber("WX",String.valueOf(map.get("project_id")));
				Map<String, Object> repair=new HashMap<>();
				repair.put("repair_number",repair_number);
				repair.put("malfunction_id",map.get("malfunction_id"));
				repair.put("repair_status",1); //待派单
				repair.put("is_repair_time",new Date()); //转维修时间
				elcmMalfunctionDao.addReapir(repair);
			}else{  //不转维修 添加处理信息
				Map<String, Object> repair=new HashMap<>();
				repair.put("malfunction_id",map.get("malfunction_id"));
				repair.put("repair_user",map.get("report_user"));
				repair.put("repair_time", DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));
				repair.put("repair_describe",map.get("repair_describe")==null?null:map.get("repair_describe"));
				repair.put("repair_url", map.get("repair_url")==null?null:map.get("repair_url"));
				elcmMalfunctionDao.updateMalfunction(repair);

				//添加故障维保记录信息
				Map<String, Object> record = new HashMap<>();
				record.put("maintenance_type_id",4);   //故障
				record.put("device_id", map.get("device_id"));
				record.put("user_id", map.get("report_user"));
				record.put("desc", map.get("repair_describe")==null?null:map.get("repair_describe"));
				record.put("complete_url", map.get("repair_url")==null?null:map.get("repair_url"));
				record.put("odd_number", number);
				webCommonService.addElcmMaintenanceRecord(record);
			}
		}
		if(result>0){
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}

	}


	/**
	 *故障详情
	 * @param
	 * @return
	 */
	@Override
	public AppResult getMalfunctionDetail(Map<String, Object> map) {
		if(map.get("malfunction_id")==null){
            return AppResult.error("1003");
		}
		map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String, Object> result=elcmMalfunctionDao.getMalfunctionDetail(map);
		if(result.get("mal_url")==null||String.valueOf(result.get("mal_url")).equals("")){
			result.put("mal_url",new ArrayList<>());
		}else{
			result.put("mal_url",String.valueOf(result.get("mal_url")).split(","));
		}
		if(result.get("repair_url")==null||String.valueOf(result.get("repair_url")).equals("")){
			result.put("repair_url",new ArrayList<>());
		}else{
			result.put("repair_url",String.valueOf(result.get("repair_url")).split(","));
		}
		return AppResult.success(result);
	}
	/**
	 *设备信息
	 * @param
	 * @return
	 */
	@Override
	public AppResult getDeviceById(Map<String, Object> map) {
        if(map.get("device_id")==null){
            return AppResult.error("1003");
        }

		Map<String, Object> deviceById = elcmMalfunctionDao.getDeviceById(map);
		return AppResult.success(deviceById);
	}



	/**
	 * 新增维修
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public AppResult addRepair(Map<String, Object> map) {
		if (map.get("device_id")==null||map.get("urgency")==null||map.get("mal_describe")==null) {
			return AppResult.error("1003");
		}
		map.put("report_user",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
		map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("is_repair",1);  //转维修
		map.put("status",1);    //转维修状态为维修中 xxx
		String number=webCommonService.getElcmOddNumber("BX",String.valueOf(map.get("project_id")));
		map.put("malfunction_number",number); //单号
		int result=elcmMalfunctionDao.reportMalfunction(map); //上报故障
		if(result>0){  //上报成功
			String repair_number=webCommonService.getElcmOddNumber("WX",String.valueOf(map.get("project_id")));
			Map<String, Object> repair=new HashMap<>();
			repair.put("repair_number",repair_number);
			repair.put("malfunction_id",map.get("malfunction_id"));
			if(map.get("repair_user")!=null&&!String.valueOf(map.get("repair_user")).equals("")) {
				repair.put("repair_status",2); //待接单
				repair.put("repair_user",map.get("repair_user"));
				repair.put("repair_original_user",map.get("repair_user"));
				repair.put("assign_time",DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));
			}else {
				repair.put("repair_status",1); //待派单
			}
			repair.put("is_repair_time",new Date()); //转维修时间
			elcmMalfunctionDao.addReapir(repair);
		}
		if(result>0){
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}
	}

}
