package com.sl.idripapp.service.impl;

import com.sl.common.utils.AppResult;
import com.sl.common.utils.JwtToken;
import com.sl.common.utils.PageUtil;
import com.sl.idripapp.dao.AccountPartsDao;
import com.sl.idripapp.entity.AccountMaterialOut;
import com.sl.idripapp.service.AccountPartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Service("AccountPartsServiceImpl")
public class AccountPartsServiceImpl implements AccountPartsService {

	@Autowired
	AccountPartsDao accountPartsDao;


	/**
	 * 获取配件、工具列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getPartsList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> partsList = accountPartsDao.getPartsList(map);
		int total = accountPartsDao.getPartsListCount(map);

		Map<String, Object> result = new HashMap<>();
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",partsList);
		return AppResult.success(result);
	}


	/**
     * 下拉框 所有配件、工具
	 * @param map
     * @return
     */
	@Override
	public AppResult getAllParts(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> result=accountPartsDao.getAllParts(map);
		return AppResult.success(result);
	}


	/**
     * 出库列表
	 * @param map
     * @return
     */
	@Override
	public AppResult getPartsOutList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> partsList = accountPartsDao.getPartsOutList(map);
		int total = accountPartsDao.getPartsOutListCount(map);

		Map<String, Object> result = new HashMap<>();
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",partsList);
		return AppResult.success(result);
	}


	/**
     * 新增出库单
	 * @param map
     * @return
     */
	@Override
	@Transactional
	public AppResult addPartsOut(Map<String, Object> map) {
		//计算配件、工具
		int parts_id=Integer.parseInt(String.valueOf(map.get("materialId")));
		int amount=accountPartsDao.getPartsAmount(parts_id);
		int receive_amount=Integer.parseInt(String.valueOf(map.get("receiveAmount")));
		if (receive_amount>amount){  //如果大于配件、工具 返回错误码
			return AppResult.error("1050");
		}

		int result=accountPartsDao.insertPartsOut(map);
		if (result>0){
			accountPartsDao.outOrInParts(amount-receive_amount,parts_id); //修改配件、工具
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
	public AppResult updatePartsOut(Map<String, Object> map) {
		//计算配件、工具  当前+之前出库-现在出库
		int parts_id=Integer.parseInt(String.valueOf(map.get("materialId")));
		int amount=accountPartsDao.getPartsAmount(parts_id);
		int receive_amount=Integer.parseInt(String.valueOf(map.get("receiveAmount")));
		int receive_amount2=accountPartsDao.getPartsOutAmount(Integer.parseInt(String.valueOf(map.get("id"))));
		if (amount+receive_amount2-receive_amount<0){
			return AppResult.error("1050");
		}

		int result=accountPartsDao.updatePartsOut(map);
		if (result>0){
			accountPartsDao.outOrInParts(amount+receive_amount2-receive_amount,parts_id); //修改配件、工具
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
	public AppResult deletePartsOut(Map<String, Object> map) {
		int result=accountPartsDao.deletePartsOut(map);
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
	public AppResult getPartsOutDetail(Map<String, Object> map) {
		AccountMaterialOut result=accountPartsDao.getPartsOutDetail(map);
			return AppResult.success(result);
	}

	//========================入库========================================//
	/**
	 * 入库列表
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getPartsInList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String,Object> result=new HashMap<>();
		ArrayList<HashMap<String, Object>> partsList = accountPartsDao.getPartsInList(map);
		int total = accountPartsDao.getPartsInListCount(map);
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",partsList);
		return AppResult.success(result);
	}


	/**
	 * 新增入库单
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public AppResult addPartsIn(Map<String, Object> map) {
		int result=accountPartsDao.insertPartsIn(map);
		if (result>0){
			//修改配件、工具
			int parts_id=Integer.parseInt(String.valueOf(map.get("materialId")));
			int amount=accountPartsDao.getPartsAmount(parts_id);
			int warehousing_amount=Integer.parseInt(String.valueOf(map.get("warehousingAmount")));
			accountPartsDao.outOrInParts(amount+warehousing_amount,parts_id);
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
	public AppResult updatePartsIn(Map<String, Object> map) {
		//修改配件、工具
		int parts_id=Integer.parseInt(String.valueOf(map.get("materialId")));
		int amount=accountPartsDao.getPartsAmount(parts_id);
		int warehousing_amount=Integer.parseInt(String.valueOf(map.get("warehousingAmount")));
		int warehousing_amount2=accountPartsDao.getPartsInAmount(Integer.parseInt(String.valueOf(map.get("id"))));

		if (amount-warehousing_amount2+warehousing_amount<0){
			return AppResult.error("1050");
		}

		int result=accountPartsDao.updatePartsIn(map);
		if (result>0){
			accountPartsDao.outOrInParts(amount-warehousing_amount2+warehousing_amount,parts_id);
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
	public AppResult deletePartsIn(Map<String, Object> map) {
		int result=accountPartsDao.deletePartsIn(map);
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
	public AppResult getPartsInDetail(Map<String, Object> map) {
		Map<String, Object> result=accountPartsDao.getPartsInDetail(map);
		return AppResult.success(result);
	}

}
