package com.sl.idripapp.service.impl;

import com.sl.common.utils.AppResult;
import com.sl.common.utils.DateUtil;
import com.sl.common.utils.JwtToken;
import com.sl.common.utils.PageUtil;
import com.sl.idripapp.dao.AccountActivityDao;
import com.sl.idripapp.entity.AccountActivityTypeTree;
import com.sl.idripapp.service.AccountActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("AccountActivityServiceImpl")
public class AccountActivityServiceImpl implements AccountActivityService {

	@Autowired
	AccountActivityDao accountActivityDao;

	/**
	 * 获取类型树
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getType(Map<String, Object> map) {
		//所有树的数据
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		List<AccountActivityTypeTree> typeList = accountActivityDao.getType(map);
		AccountActivityTypeTree elcmDeviceTypeTree=new AccountActivityTypeTree();
		//递归获取树结构
		List<AccountActivityTypeTree> result=elcmDeviceTypeTree.getAccountActivityTreeByRecursion(0,typeList,0);
		return AppResult.success(result);
	}

	@Override
	public AppResult getDate(Map<String, Object> map) {
		String yearDate=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY);
		int year=Integer.parseInt(yearDate)-1;
		int lastYear=year-1;
		List<Integer> list=new ArrayList<>();
		list.add(year);
		list.add(lastYear);
		return AppResult.success(list);
	}

	/**
	 * 班组列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getActivityList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		if (map.get("date")!=null&&!map.get("date").toString().equals("")){
			map.put("begin_time",map.get("date")+"-01-01 00:00:00");
			map.put("end_time",map.get("date")+"-12-31 23:59:59");
		}
		if (map.get("typeId")!=null&&!map.get("typeId").toString().equals("")){
			int type_id=Integer.parseInt(String.valueOf(map.get("typeId")));
			//所有树的数据
			List<AccountActivityTypeTree>  typeList = accountActivityDao.getType(map);
			AccountActivityTypeTree elcmDeviceTypeTree=new AccountActivityTypeTree();
			//递归获取树结构
			List<AccountActivityTypeTree> typeTree=elcmDeviceTypeTree.getAccountActivityTreeByRecursion(type_id,typeList,0);
			//需要查询的类型
			List<Integer> type=new ArrayList<>();
			getTypeTreeList(type,typeTree);
			type.add(type_id);
			map.put("type",type);
		}

		ArrayList<HashMap<String, Object>> list = accountActivityDao.getActivityList(map);
		int total = accountActivityDao.getActivityListCount(map);

		Map<String, Object> result = new HashMap<>();
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",list);
		return AppResult.success(result);
	}


	//遍历类型 返回类型线性结构
	public void getTypeTreeList(List<Integer> typeList,List<AccountActivityTypeTree> typeTree){
		if (typeTree!=null&&typeTree.size()!=0){
			for (AccountActivityTypeTree type:typeTree){
				typeList.add(type.getId());
				getTypeTreeList(typeList,type.getChilds());
			}
		}
	}

}
