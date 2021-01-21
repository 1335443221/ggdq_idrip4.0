package com.sl.idripapp.service.impl;

import com.sl.common.utils.AppResult;
import com.sl.common.utils.JwtToken;
import com.sl.common.utils.PageUtil;
import com.sl.idripapp.dao.AccountLaborDao;
import com.sl.idripapp.entity.AccountMaterialOut;
import com.sl.idripapp.service.AccountLaborService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Service("AccountLaborServiceImpl")
public class AccountLaborServiceImpl implements AccountLaborService {

	@Autowired
	AccountLaborDao accountLaborDao;


	/**
	 * 获取劳保列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getLaborList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> laborList = accountLaborDao.getLaborList(map);
		int total = accountLaborDao.getLaborListCount(map);

		Map<String, Object> result = new HashMap<>();
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",laborList);
		return AppResult.success(result);
	}


	/**
     * 下拉框 所有劳保
	 * @param map
     * @return
     */
	@Override
	public AppResult getAllLabor(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> result=accountLaborDao.getAllLabor(map);
		return AppResult.success(result);
	}


	/**
     * 出库列表
	 * @param map
     * @return
     */
	@Override
	public AppResult getLaborOutList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> laborList = accountLaborDao.getLaborOutList(map);
		int total = accountLaborDao.getLaborOutListCount(map);

		Map<String, Object> result = new HashMap<>();
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",laborList);
		return AppResult.success(result);
	}


	/**
     * 新增出库单
	 * @param map
     * @return
     */
	@Override
	@Transactional
	public AppResult addLaborOut(Map<String, Object> map) {
		//计算劳保
		int labor_id=Integer.parseInt(String.valueOf(map.get("materialId")));
		int amount=accountLaborDao.getLaborAmount(labor_id);
		int receive_amount=Integer.parseInt(String.valueOf(map.get("receiveAmount")));
		if (receive_amount>amount){  //如果大于劳保 返回错误码
			return AppResult.error("1050");
		}

		int result=accountLaborDao.insertLaborOut(map);
		if (result>0){
			accountLaborDao.outOrInLabor(amount-receive_amount,labor_id); //修改劳保
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}
	}


	/**
     * 修改出库
	 * @param map
     * @return
     */
	@Override
	public AppResult updateLaborOut(Map<String, Object> map) {
		//计算劳保  当前+之前出库-现在出库
		int labor_id=Integer.parseInt(String.valueOf(map.get("materialId")));
		int amount=accountLaborDao.getLaborAmount(labor_id);
		int receive_amount=Integer.parseInt(String.valueOf(map.get("receiveAmount")));
		int receive_amount2=accountLaborDao.getLaborOutAmount(Integer.parseInt(String.valueOf(map.get("id"))));
		if (amount+receive_amount2-receive_amount<0){
			return AppResult.error("1050");
		}

		int result=accountLaborDao.updateLaborOut(map);
		if (result>0){
			accountLaborDao.outOrInLabor(amount+receive_amount2-receive_amount,labor_id); //修改劳保
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}
	}

	/**
     * 删除出库
	 * @param map
     * @return
     */
	@Override
	public AppResult deleteLaborOut(Map<String, Object> map) {
		int result=accountLaborDao.deleteLaborOut(map);
		if (result>0){
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}
	}


	/**
     * 出库
	 * @param map
     * @return
     */
	@Override
	public AppResult getLaborOutDetail(Map<String, Object> map) {
		AccountMaterialOut result=accountLaborDao.getLaborOutDetail(map);
			return AppResult.success(result);
	}

	//========================入库========================================//
	/**
	 * 入库列表
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getLaborInList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String,Object> result=new HashMap<>();
		ArrayList<HashMap<String, Object>> laborList = accountLaborDao.getLaborInList(map);
		int total = accountLaborDao.getLaborInListCount(map);
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",laborList);
		return AppResult.success(result);
	}


	/**
	 * 新增入库单
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public AppResult addLaborIn(Map<String, Object> map) {
		int result=accountLaborDao.insertLaborIn(map);
		if (result>0){
			//修改劳保
			int labor_id=Integer.parseInt(String.valueOf(map.get("materialId")));
			int amount=accountLaborDao.getLaborAmount(labor_id);
			int warehousing_amount=Integer.parseInt(String.valueOf(map.get("warehousingAmount")));
			accountLaborDao.outOrInLabor(amount+warehousing_amount,labor_id);
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}
	}

	/**
	 * 修改入库
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public AppResult updateLaborIn(Map<String, Object> map) {
		//修改劳保
		int labor_id=Integer.parseInt(String.valueOf(map.get("materialId")));
		int amount=accountLaborDao.getLaborAmount(labor_id);
		int warehousing_amount=Integer.parseInt(String.valueOf(map.get("warehousingAmount")));
		int warehousing_amount2=accountLaborDao.getLaborInAmount(Integer.parseInt(String.valueOf(map.get("id"))));

		if (amount-warehousing_amount2+warehousing_amount<0){
			return AppResult.error("1050");
		}

		int result=accountLaborDao.updateLaborIn(map);
		if (result>0){
			accountLaborDao.outOrInLabor(amount-warehousing_amount2+warehousing_amount,labor_id);
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}
	}

	/**
	 * 删除入库
	 * @param map
	 * @return
	 */
	@Override
	public AppResult deleteLaborIn(Map<String, Object> map) {
		int result=accountLaborDao.deleteLaborIn(map);
		if (result>0){
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}
	}

	/**
	 * 入库
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getLaborInDetail(Map<String, Object> map) {
		Map<String, Object> result=accountLaborDao.getLaborInDetail(map);
		return AppResult.success(result);
	}

}
