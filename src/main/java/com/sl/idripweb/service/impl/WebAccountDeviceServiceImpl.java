package com.sl.idripweb.service.impl;

import com.sl.common.utils.PageUtil;
import com.sl.common.utils.ResultHandleUtil;
import com.sl.common.utils.WebResult;
import com.sl.idripweb.dao.WebAccountDeviceDao;
import com.sl.idripweb.service.WebAccountDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("WebAccountMaintenanceServiceImpl")
public class WebAccountDeviceServiceImpl implements WebAccountDeviceService {

	@Autowired
	WebAccountDeviceDao webAccountDeviceDao;

	/**
	 * 获取所有设备
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getAllDevice(Map<String, Object> map) {
        //所有树的数据
        List<Map<String, Object>>  list = webAccountDeviceDao.getAllDevice(map);
        return WebResult.success(list);
	}

	/**
	 * 周期
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getDeviceCycle(Map<String, Object> map) {
		//所有树的数据
		List<Map<String, Object>>  list = webAccountDeviceDao.getDeviceCycle(map);
		return WebResult.success(list);
	}

	/**
	 * 时间
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getDeviceRemind(Map<String, Object> map) {
		//所有树的数据
		List<Map<String, Object>>  list = webAccountDeviceDao.getDeviceRemind(map);
		return WebResult.success(list);
	}

	/**
	 * 频率
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getDeviceFrequency(Map<String, Object> map) {
		//所有树的数据
		List<Map<String, Object>>  list = webAccountDeviceDao.getDeviceFrequency(map);
		return WebResult.success(list);
	}


	/**
	 * 新增设备
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult addDevice(Map<String, Object> map) {
        int result= webAccountDeviceDao.addDevice(map);
		if (result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}



	/**
	 * 修改设备
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult updateDevice(Map<String, Object> map) {
        int result= webAccountDeviceDao.updateDevice(map);
		if (result>0){
			return WebResult.success(200,"编辑成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 删除设备
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult deleteDevice(Map<String, Object> map) {
		int result=webAccountDeviceDao.deleteDevice(map);
		if (result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 获取设备详情
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getDevice(Map<String, Object> map) {
		Map<String, Object> result=webAccountDeviceDao.getDevice(map.get("id").toString());
		return WebResult.success(result);
	}


	/**
	 * 获取设备详情
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getMaintenanceStatus(Map<String, Object> map) {
		List<Map<String, Object>> maintenanceStatus = webAccountDeviceDao.getMaintenanceStatus(map);
		return WebResult.success(maintenanceStatus);
	}





	/**
	 * 新增维保
	 * @param
	 * @return
	 */
	@Override
    @Transactional
	public WebResult addMaintenance(Map<String, Object> map) {
        Map<String, Object> device = webAccountDeviceDao.getDevice(String.valueOf(map.get("device_id")));
        if (device.get("last_status").toString().equals("2")){
            return WebResult.error(621);
        }
        /*//获取上一次维保记录的时间和维保人
        Map<String, Object> maintenanceDesc = webAccountDeviceDao.getMaintenanceByDeviceDesc(map.get("device_id").toString());
       if (maintenanceDesc==null){
           map.put("last_maintenance_time",device.get("last_maintenance_time"));
           map.put("last_maintainer",device.get("last_maintainer"));
       }else{
           map.put("last_maintenance_time",maintenanceDesc.get("maintenance_time"));
           map.put("last_maintainer",maintenanceDesc.get("maintainer"));
       }*/
        //添加维保
		int result= webAccountDeviceDao.addMaintenance(map);
        //把设备信息改为新增维保的时间/状态/人员
        webAccountDeviceDao.updateDeviceByMaintenance(map);
		if (result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 列表
	 * @param
	 * @return
	 */
	@Override
	public WebResult getMaintenanceList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
        if (map.get("date")!=null&&!map.get("date").toString().equals("")){
            map.put("begin_time",map.get("date")+"-01-01 00:00:00");
            map.put("end_time",map.get("date")+"-12-31 23:59:59");
        }
		ArrayList<HashMap<String, Object>>  list= webAccountDeviceDao.getMaintenanceList(map);
		int  total= webAccountDeviceDao.getMaintenanceListCount(map);
		ResultHandleUtil.addOrderNumber(list, map);  //为结果集添加序号

		Map<String,Object> result=new HashMap<>(2);
		result.put("list",list);
		result.put("total",total);
		return WebResult.success(result);
	}

	/**
	 * 修改维保
	 * @param
	 * @return
	 */
	@Override
    @Transactional
	public WebResult updateMaintenance(Map<String, Object> map) {
        //修改维保
        int result= webAccountDeviceDao.updateMaintenance(map);

        //把设备信息改为新增维保的时间/状态/人员
        webAccountDeviceDao.updateDeviceByMaintenance(map);

		if (result>0){
			return WebResult.success(200,"编辑成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 删除维保
	 * @param
	 * @return
	 */
	@Override
	public WebResult deleteMaintenance(Map<String, Object> map) {
		List<String> idList=Arrays.asList(map.get("ids").toString().split(","));
		int result=webAccountDeviceDao.deleteMaintenance(idList);
		if (result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@Override
	public WebResult statistics(Map<String, Object> map) {
        String pieLegend[]={"正常","重大故障"};
        String date=map.get("date").toString();
        String project_id=map.get("project_id").toString();

        //饼图数据==========================
        List<Map<String, Object>> faultList = webAccountDeviceDao.getMaintenanceGroupByFault(date, project_id);
        Map<String, Object> faultMap=new HashMap<>();
        for (Map<String, Object> f:faultList){
            faultMap.put(f.get("is_fault").toString(),f.get("count"));
        }

        List<Map<String, Object>> pieList=new ArrayList<>();
        Map<String, Object> pieMap=new HashMap<>();
        pieMap.put("name","正常");
        pieMap.put("value",faultMap.get("0")==null?0:faultMap.get("0"));
        pieList.add(pieMap);

        pieMap=new HashMap<>();
        pieMap.put("name","重大故障");
        pieMap.put("value",faultMap.get("1")==null?0:faultMap.get("1"));
        pieList.add(pieMap);

        Map<String,Object> pie=new HashMap<>();
        pie.put("date",pieList);
        pie.put("legend",pieLegend);

        //排名==========================
        List<Map<String, Object>> rankingList =webAccountDeviceDao.getMaintenanceByStatusRanking(date, project_id);
        List<String> rankingData=new ArrayList<>();
        List<String> rankingXAxis=new ArrayList<>();
        for (Map<String, Object> r:rankingList){
            rankingData.add(r.get("count").toString());
            rankingXAxis.add(r.get("name").toString());
        }

        Map<String, Object> ranking=new HashMap<>();
        ranking.put("xAxis",rankingXAxis); //横坐标
        ranking.put("data",rankingData); //数据

        //对比==========================
        String contrastxAxis[]={"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"};
        String contrastLegend[]={"已完成","故障延期"};
        List<Map<String, Object>> contrastList =webAccountDeviceDao.getMaintenanceByContrast(date, project_id);
        Map<Integer, Integer> completeData=new HashMap<>();
        Map<Integer, Integer> faultData=new HashMap<>();

        for (Map<String, Object> c:contrastList){
            if (c.get("status").toString().equals("1")){
                completeData.put(Integer.parseInt(c.get("time").toString()),Integer.parseInt(c.get("count").toString()));
            }else if (c.get("status").toString().equals("2")){
                faultData.put(Integer.parseInt(c.get("time").toString()),Integer.parseInt(c.get("count").toString()));
            }
        }

        List<Integer> complete=new ArrayList<>();
        List<Integer> fault=new ArrayList<>();
        for (int i=1;i<13;i++){
            complete.add(completeData.get(i)==null?0:completeData.get(i));
            fault.add(faultData.get(i)==null?0:faultData.get(i));
        }
        Map<String, Object> contrast=new HashMap<>();
        contrast.put("xAxis",contrastxAxis);
        contrast.put("legend",contrastLegend);
        contrast.put("fault",fault);
        contrast.put("complete",complete);

        //==============返回数据
        Map<String,Object> result=new HashMap<>(3);
        result.put("pie",pie);
        result.put("ranking",ranking);
        result.put("contrast",contrast);
        return WebResult.success(result);
	}


}
