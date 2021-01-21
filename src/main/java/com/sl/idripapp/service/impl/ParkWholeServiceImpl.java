package com.sl.idripapp.service.impl;

import com.sl.common.config.ConstantConfig;
import com.sl.common.utils.*;
import com.sl.idripapp.dao.ParkWholeDao;
import com.sl.idripapp.entity.TaskPark;
import com.sl.idripapp.service.ParkWholeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;


/**
 * TODO 每次新项目, 需要更新writeApkAndIndexHtml方法 同时更新activeVersion.html中的二维码部分
 * app版本
 * @author 李旭日
 */
@Service("parkWholeServiceImpl")
public class ParkWholeServiceImpl implements ParkWholeService {
	@Autowired
	ParkWholeDao parkWholeDao;
	@Autowired
	ConstantConfig constantConfig;

	/**
	 *健康指数
	 */
	@Override
	public AppResult healthIndex(Map<String, Object> map) {
		//** 评判标准
		// 1.优秀: 1%以下的故障或报警率
		// 2.良好: 15%~1%的故障或报警率
		// 3.及格: 15%以上的故障或报警或报警率
		map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
		List<String> list=new ArrayList<>();
		list.add("1");list.add("2");list.add("3");list.add("4");list.add("6");  //除了报废的设备
		map.put("list",list);
		int count=parkWholeDao.getDeviceInfoCount(map);

		list=new ArrayList<>();
		list.add("2");  //维修中的设备
		map.put("list",list);
		int repairCount=parkWholeDao.getDeviceInfoCount(map);
		double fraction=0.0;
		if (count!=0){
			fraction=(double)Math.round(((double)repairCount/count) * 10000)/100;  //故障率
		}
		String state="";
		if (fraction<1){
			state="优秀";
		}
		if (fraction>=1&&fraction<=15){
			state="良好";
		}
		if (fraction>15){
			state="及格";
		}
		Map<String, Object> result=new HashMap<>(2);
		result.put("fraction",100-fraction);  //分数
		result.put("state",state);  //状态
		return AppResult.success(result);
	}

	/**
	 *运维状态
	 */
	@Override
	public AppResult operationAndMaintenance(Map<String, Object> map) {
		DecimalFormat df = new DecimalFormat("0.00");

		//水电气
		String nowYear=String.valueOf(DateUtil.getYear(new Date()));
		String lastYear= String.valueOf(DateUtil.getYear(new Date())-1);
		map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("edate", nowYear); //今年
		map.put("bdate", lastYear); //去年
		//电
		map.put("dataTable","year_data_ep");
		map.put("meterTable","elec_meter");
		List<Map<String, Object>> elecEnergyList=parkWholeDao.getParkYearEnergy(map);
		Map<String, Double> elecEnergy=new HashMap<>();
		for (Map<String, Object> energy:elecEnergyList){
			elecEnergy.put(energy.get("date").toString(),Double.parseDouble(energy.get("power").toString()));
		}
		double nowElecEnergy=elecEnergy.get(nowYear)==null?0.0:elecEnergy.get(nowYear);
		double lastElecEnergy=elecEnergy.get(lastYear)==null?0.0:elecEnergy.get(lastYear);


		//水
		map.put("dataTable","year_data_qf");
		map.put("meterTable","water_meter");
		List<Map<String, Object>> waterEnergyList=parkWholeDao.getParkYearEnergy(map);
		Map<String, Double> waterEnergy=new HashMap<>();
		for (Map<String, Object> energy:waterEnergyList){
			waterEnergy.put(energy.get("date").toString(),Double.parseDouble(energy.get("power").toString()));
		}
		double nowWaterEnergy=waterEnergy.get(nowYear)==null?0.0:waterEnergy.get(nowYear);
		double lastWaterEnergy=waterEnergy.get(lastYear)==null?0.0:waterEnergy.get(lastYear);

		//气
		map.put("dataTable","year_data_gqf");
		map.put("meterTable","gas_meter");
		List<Map<String, Object>> gasEnergyList=parkWholeDao.getParkYearEnergy(map);
		Map<String, Double> gasEnergy=new HashMap<>();
		for (Map<String, Object> energy:gasEnergyList){
			gasEnergy.put(energy.get("date").toString(),Double.parseDouble(energy.get("power").toString()));
		}
		double nowGasEnergy=gasEnergy.get(nowYear)==null?0.0:gasEnergy.get(nowYear);
		double lastGasEnergy=gasEnergy.get(lastYear)==null?0.0:gasEnergy.get(lastYear);

		double nowParkEnergy=nowElecEnergy * 0.1229 + nowWaterEnergy * 0.2429 + nowGasEnergy * 1.2997;
		double lastParkEnergy=lastElecEnergy * 0.1229 + lastWaterEnergy * 0.2429 + lastGasEnergy * 1.2997;

		//能耗数据
		Map<String, Object> energyResult=new LinkedHashMap<>(8);
		energyResult.put("parkEnergy",df.format(nowParkEnergy)+"tce");  //今年园区能耗
		energyResult.put("elecEnergy",df.format(nowElecEnergy)+"kWh");  //今年电
		energyResult.put("waterEnergy",df.format(nowWaterEnergy)+"t");  //今年水
		energyResult.put("gasEnergy",df.format(nowGasEnergy)+"m³");  //今年气

		String elecPercentage=df.format((nowElecEnergy * 0.1229/nowParkEnergy)*100);
		String waterPercentage=df.format((nowWaterEnergy * 0.2429 /nowParkEnergy)*100);
		String gasPercentage=df.format((nowGasEnergy * 1.2997/nowParkEnergy)*100);
		energyResult.put("elecPercentage",elecPercentage+"%");  //百分比
		energyResult.put("waterPercentage",waterPercentage+"%");  //百分比
		energyResult.put("gasPercentage",gasPercentage+"%");  //百分比


		if (nowElecEnergy>=lastParkEnergy){
			energyResult.put("compare","增加"+df.format(nowElecEnergy-lastParkEnergy)+"tce");  //今年相对于去年
		}else{
			energyResult.put("compare","减少"+df.format(lastParkEnergy-nowElecEnergy)+"tce");  //今年相对于去年
		}

		List<String> list=new ArrayList<>();
		list.add("1");list.add("2");list.add("3");list.add("4");list.add("6");  //除了报废的设备
		map.put("list",list);
		int count=parkWholeDao.getDeviceInfoCount(map);  //总设备数


		list=new ArrayList<>();
		list.add("2");
		map.put("list",list);
		int repairCount=parkWholeDao.getDeviceInfoCount(map);  //维修中的设备
		double fraction=0.0;
		if (count!=0){
			fraction=((double)repairCount/count) * 100;  //故障率
		}

		list=new ArrayList<>();
		list.add("1");list.add("2");list.add("3");list.add("4");  //除了报废和停用的设备
		map.put("list",list);
		int intactCount=parkWholeDao.getDeviceInfoCount(map);  //除了报废和停用的设备设备数

		double intact=100.0;
		if (count!=0){
			intact=((double)intactCount/count) * 100;  //完好率
		}

		Map<String, Object> deviceResult=new LinkedHashMap<>(2);
		deviceResult.put("faultRate",df.format(fraction)+"%");  //故障率
		deviceResult.put("intactRate",df.format(intact)+"%");  //完好率
		deviceResult.put("deviceCount",count);  //设备数量


		List<Map<String, Object>> deviceTaskRecord =parkWholeDao.getAllDeviceTaskRecord(map);
		List<Map<String, Object>> deviceRepair =parkWholeDao.getAllDeviceRepair(map);
		List<Map<String, Object>> firePatrolLog =parkWholeDao.getAllFirePatrolLog(map);
		int taskRecordCount=deviceTaskRecord.size();  //维保
		int deviceRepairCount=deviceRepair.size();   //维修
		int firePatrolLogCount=firePatrolLog.size();   //巡检

		double completeRate=100.00;
		if (firePatrolLogCount!=0){
			int firePatrolLogComplete=0;
			for (Map<String, Object> log:firePatrolLog){
				if (log.get("state").toString().equals("1")||log.get("state").toString().equals("2")){
					firePatrolLogComplete++;
				}
			}
			 completeRate=((double)firePatrolLogComplete/firePatrolLogCount) *100; //完成率
		}

		//平均时间
		double averageTime=parkWholeDao.getMalRepairAverageTime(map);
		Map<String, Object> taskResult=new LinkedHashMap<>(5);
		taskResult.put("maintenanceCount",taskRecordCount); //维保
		taskResult.put("repairCount",deviceRepairCount); //维修
		taskResult.put("patrolCount",firePatrolLogCount); //维修
		taskResult.put("taskcount",taskRecordCount+deviceRepairCount+firePatrolLogCount); //任务次数

		taskResult.put("completeRate",df.format(completeRate)+"%"); //完成率
		taskResult.put("averageTime",averageTime+"min"); //平均时间


		Map<String, Object> result=new LinkedHashMap<>(3);
		result.put("energy",energyResult);  //能耗
		result.put("device",deviceResult);  //故障
		result.put("task",taskResult);  //任务
		return AppResult.success(result);
	}

	/**
	 * 意见反馈
	 * @param map
	 * @return
	 */
	@Override
	public AppResult addOpinion(Map<String, Object> map) {
		if (map.get("userName")==null||map.get("phone")==null){
			return AppResult.error("1003");
		}
		if (map.get("opinionDesc")==null){
			map.put("opinionDesc","");
		}

		map.put("route",2);  //渠道
		map.put("img","");  //图片 默认空
		parkWholeDao.addOpinion(map);
		return AppResult.success();
	}


	/**
	 * 任务类型
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getTaskType(Map<String, Object> map) {
		List<Map<String, Object>> taskType = parkWholeDao.getTaskType(map);
		return AppResult.success(taskType);
	}
	/**
	 * 任务状态
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getTaskStatus(Map<String, Object> map) {
		List<Map<String, Object>> taskType = parkWholeDao.getTaskStatus(map);
		return AppResult.success(taskType);
	}

	/**
	 * 任务列表
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getTaskList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码

		String token=String.valueOf(map.get("token"));
		map.put("appUserId",JwtToken.getUserId(token));  //app user_id
		map.put("userId",JwtToken.getProjectUserId(token)); //project user_id
		map.put("projectId",JwtToken.getProjectId(token)); //project_id

		List<TaskPark> deviceTask = parkWholeDao.getMyDeviceTask(map); //设备任务
		List<TaskPark> fireTask = parkWholeDao.getMyFireTask(map); //设备任务

		deviceTask.addAll(fireTask); //全部数据

		int total=deviceTask.size();

		Map<String, Object> result=new LinkedHashMap<>();
		int fromNum=Integer.parseInt(String.valueOf(map.get("fromNum")));
		int pageSize=Integer.parseInt(String.valueOf(map.get("pageSize")));
		if((fromNum+pageSize)>=total){
			result.put("isLastPage",true);
		}else{
			result.put("isLastPage",false);
		}
		//手动排序
		ListSort.parkTaskSortByStatus(deviceTask);
		List<TaskPark> resultList=new ArrayList<>();
		//手动分页
		for (int i=fromNum;i<fromNum+pageSize;i++){
			if (total>i){
				resultList.add(deviceTask.get(i));
			}
		}
		//处理数据
		for (int i=0;i<resultList.size();i++){
			if (resultList.get(i).getTaskDevice().getDeviceName()==null){
				resultList.get(i).setTaskDevice(null);
			}
			if (resultList.get(i).getTaskFire().getPatrolName()==null){
				resultList.get(i).setTaskFire(null);
			}
		}
		result.put("taskList",resultList);
		return AppResult.success(result);
	}



}
