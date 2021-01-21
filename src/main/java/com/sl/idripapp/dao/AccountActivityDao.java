package com.sl.idripapp.dao;

import com.sl.idripapp.entity.AccountActivityTypeTree;
import com.sl.idripapp.entity.AccountMaterialOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface AccountActivityDao {

    List<AccountActivityTypeTree> getType(Map<String, Object> map);

    ArrayList<HashMap<String, Object>> getActivityList(Map<String, Object> map);
    Integer getActivityListCount(Map<String, Object> map);
}
