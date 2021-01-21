package com.sl.idripapp.service.impl;

import com.sl.common.utils.*;
import com.sl.idripapp.dao.AccountStockDao;
import com.sl.idripapp.entity.AccountMaterialOut;
import com.sl.idripapp.service.AccountStockService;
import com.sl.idripweb.dao.WebAccountStockDao;
import com.sl.idripweb.service.WebAccountStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("AccountStockServiceImpl")
public class AccountStockServiceImpl implements AccountStockService {

	@Autowired
	AccountStockDao accountStockDao;


	/**
	 * 获取库存列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getStockList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> stockList = accountStockDao.getStockList(map);
		int total = accountStockDao.getStockListCount(map);

		Map<String, Object> result = new HashMap<>();
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",stockList);
		return AppResult.success(result);
	}


	/**
     * 下拉框 所有库存
	 * @param map
     * @return
     */
	@Override
	public AppResult getAllStock(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> result=accountStockDao.getAllStock(map);
		return AppResult.success(result);
	}


	/**
     * 出库列表
	 * @param map
     * @return
     */
	@Override
	public AppResult getStockOutList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> stockList = accountStockDao.getStockOutList(map);
		int total = accountStockDao.getStockOutListCount(map);

		Map<String, Object> result = new HashMap<>();
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",stockList);
		return AppResult.success(result);
	}


	/**
     * 新增出库单
	 * @param map
     * @return
     */
	@Override
	@Transactional
	public AppResult addStockOut(Map<String, Object> map) {
		//计算库存
		int stock_id=Integer.parseInt(String.valueOf(map.get("materialId")));
		int amount=accountStockDao.getStockAmount(stock_id);
		int receive_amount=Integer.parseInt(String.valueOf(map.get("receiveAmount")));
		if (receive_amount>amount){  //如果大于库存 返回错误码
			return AppResult.error("1050");
		}

		int result=accountStockDao.insertStockOut(map);
		if (result>0){
			accountStockDao.outOrInStock(amount-receive_amount,stock_id); //修改库存
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
	public AppResult updateStockOut(Map<String, Object> map) {
		//计算库存  当前+之前出库-现在出库
		int stock_id=Integer.parseInt(String.valueOf(map.get("materialId")));
		int amount=accountStockDao.getStockAmount(stock_id);
		int receive_amount=Integer.parseInt(String.valueOf(map.get("receiveAmount")));
		int receive_amount2=accountStockDao.getStockOutAmount(Integer.parseInt(String.valueOf(map.get("id"))));
		if (amount+receive_amount2-receive_amount<0){
			return AppResult.error("1050");
		}

		int result=accountStockDao.updateStockOut(map);
		if (result>0){
			accountStockDao.outOrInStock(amount+receive_amount2-receive_amount,stock_id); //修改库存
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
	public AppResult deleteStockOut(Map<String, Object> map) {
		int result=accountStockDao.deleteStockOut(map);
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
	public AppResult getStockOutDetail(Map<String, Object> map) {
		AccountMaterialOut result=accountStockDao.getStockOutDetail(map);
			return AppResult.success(result);
	}

	//========================入库========================================//
	/**
	 * 入库列表
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getStockInList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String,Object> result=new HashMap<>();
		ArrayList<HashMap<String, Object>> stockList = accountStockDao.getStockInList(map);
		int total = accountStockDao.getStockInListCount(map);
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",stockList);
		return AppResult.success(result);
	}


	/**
	 * 新增入库单
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public AppResult addStockIn(Map<String, Object> map) {
		int result=accountStockDao.insertStockIn(map);
		if (result>0){
			//修改库存
			int stock_id=Integer.parseInt(String.valueOf(map.get("materialId")));
			int amount=accountStockDao.getStockAmount(stock_id);
			int warehousing_amount=Integer.parseInt(String.valueOf(map.get("warehousingAmount")));
			accountStockDao.outOrInStock(amount+warehousing_amount,stock_id);
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
	public AppResult updateStockIn(Map<String, Object> map) {
		//修改库存
		int stock_id=Integer.parseInt(String.valueOf(map.get("materialId")));
		int amount=accountStockDao.getStockAmount(stock_id);
		int warehousing_amount=Integer.parseInt(String.valueOf(map.get("warehousingAmount")));
		int warehousing_amount2=accountStockDao.getStockInAmount(Integer.parseInt(String.valueOf(map.get("id"))));

		if (amount-warehousing_amount2+warehousing_amount<0){
			return AppResult.error("1050");
		}

		int result=accountStockDao.updateStockIn(map);
		if (result>0){
			accountStockDao.outOrInStock(amount-warehousing_amount2+warehousing_amount,stock_id);
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
	public AppResult deleteStockIn(Map<String, Object> map) {
		int result=accountStockDao.deleteStockIn(map);
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
	public AppResult getStockInDetail(Map<String, Object> map) {
		Map<String, Object> result=accountStockDao.getStockInDetail(map);
		return AppResult.success(result);
	}

}
