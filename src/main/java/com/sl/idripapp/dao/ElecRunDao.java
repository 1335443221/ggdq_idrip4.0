package com.sl.idripapp.dao;

import com.sl.common.entity.OperationLogs;
import com.sl.common.entity.SjfEpFees;
import com.sl.common.entity.SjfYesterdayData;
import com.sl.common.entity.params.SetValParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ElecRunDao {
    //获取区域用能 分类的 配电室列表
    List<Map<String,Object>> getCategoryTransformerroom(Map<String,Object> map);

    // 获取用电监控配电室及进线列表
    ArrayList<Map<String, Object>> getElecRoomAndLineListByRid(Map<String, Object> map);
    int getElecRoomAndLineListByRidCount(Map<String, Object> map);
    //获取出线数据
    ArrayList<Map<String, Object>> getChildrenCategoryList(@Param(value="parent_category_id")String parent_category_id);


}
